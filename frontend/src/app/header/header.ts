import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../services/auth';
import { NotificationItem } from '../models/notification';
import { catchError, interval, of, startWith, Subject, Subscription, switchMap, takeUntil, timer } from 'rxjs';
import { NotificationService } from '../services/notification';
import { FormsModule } from '@angular/forms';
import { MatMenuModule } from '@angular/material/menu';
import { MatButtonModule } from '@angular/material/button';
import { MatBadgeModule } from '@angular/material/badge';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { HttpParams } from '@angular/common/http';
import { AlertService } from '../services/alert-service';

@Component({
  selector: 'app-header',
  imports: [RouterModule, CommonModule, FormsModule, MatMenuModule,
    MatButtonModule,
    MatBadgeModule,
    MatIconModule,
    MatListModule],
  templateUrl: './header.html',
  styleUrl: './header.css'
})
export class HeaderComponent implements OnInit, OnDestroy {

  notifications: NotificationItem[] = [];
  unreadCount = 0;
  dropdownOpen = false;
  defaultAvatar = '/assets/default-avatar.png'; // change as needed
  loadingMore = false;
  allLoaded = false;
  lastNotifId: number = 0;
  pageSize: number = 10;
  private scrollTimer: any;

  private destroy$ = new Subject<void>();
  private pollingSub?: Subscription;
  darkMode = false;

  constructor(
    public authService: AuthService,
    private notificationService: NotificationService,
    private router: Router,
    private myAlert : AlertService
  ) { }

  ngOnInit(): void {
    // Start polling only if user is logged in
    // if (this.authService.isLoggedIn()) {
    this.startPolling();
    // }
    console.log("*********************", this.authService.isAdmin());

    // If your AuthService provides an auth state Observable, subscribe to it to start/stop polling on login/logout.
    // Example (uncomment if available):
    // this.authService.authState$.pipe(takeUntil(this.destroy$)).subscribe(isLoggedIn => {
    //   if (isLoggedIn) this.startPolling();
    //   else this.stopPolling();
    // });
    const saved = localStorage.getItem('darkMode') === 'true';
    this.darkMode = saved;
    document.body.classList.toggle('dark-theme', this.darkMode);
  }


  toggleDarkMode() {
    this.darkMode = !this.darkMode;
    document.body.classList.toggle('dark-theme', this.darkMode);
    localStorage.setItem('darkMode', String(this.darkMode));
  }


  startPolling(): void {
    // ensure previous subscription stopped
    this.stopPolling();
    this.lastNotifId = 0;

    // Start immediately, then every 30s
    this.pollingSub = timer(0, 1000000000).pipe(
      takeUntil(this.destroy$),
      switchMap(() => {
        if (!this.authService.isLoggedIn()) return of([] as NotificationItem[]);
        return this.notificationService.getNotifications(this.lastNotifId, this.pageSize)
          .pipe(catchError(err => {
            console.error('Polling error', err);
            return of([] as NotificationItem[]);
          }));
      })
    ).subscribe(notifs => {
      console.log("notifs:", notifs);
      if (notifs.length > 0) {
        const oldestNotif = notifs.reduce((min, p) => p.id < min.id ? p : min, notifs[0]);
        this.lastNotifId = oldestNotif.id;
        this.applyNotifications(notifs);
      }
    });
  }

  stopPolling(): void {
    if (this.pollingSub) {
      this.pollingSub.unsubscribe();
      this.pollingSub = undefined;
    }
  }

  applyNotifications(notifs: NotificationItem[]): void {
    // Sort by newest first (optional)
    this.notifications = (notifs || []).sort((a, b) => {
      return new Date(b.postCreatedAt).getTime() - new Date(a.postCreatedAt).getTime();
    });

    this.unreadCount = this.notifications.filter(n => n.unread).length;
  }

  toggleDropdown(): void {
    this.dropdownOpen = !this.dropdownOpen;
    if (this.dropdownOpen) {
      this.notificationService.markAllAsReadFromDropDown().subscribe(res => {
        if (!res || !res.success) {
          this.myAlert.showAlert("Error", "Cannot mark all notifications as read, try again later!");

          return;
        }
        this.unreadCount = 0;
      });
    }

    // if (this.dropdownOpen) {
    //   // user opened dropdown -> mark unread as read automatically
    //   this.autoMarkOpenItemsRead();
    // }
  }

  closeDropdown(): void {
    this.dropdownOpen = false;
  }

  // autoMarkOpenItemsRead(): void {
  //   const unreadIds = this.notifications.filter(n => n.unread).map(n => n.id);
  //   if (unreadIds.length === 0) return;

  //   // Optimistic update: mark local items as read immediately
  //   this.notifications = this.notifications.map(n => ({ ...n, unread: false }));
  //   this.unreadCount = 0;

  //   this.notificationService.markAsRead(unreadIds).subscribe(result => {
  //     if (!result || !result.success) {
  //       // Re-fetch to sync server state (or revert)
  //       this.notificationService.getNotifications().subscribe(n => this.applyNotifications(n));
  //     }
  //   });
  // }

  // markAllRead(event?: Event): void {
  //   if (event) event.stopPropagation();
  markAllRead(): void {
    this.notificationService.markAllAsRead().subscribe(res => {
      if (!res || !res.success) {
        this.myAlert.showAlert("Error", "Cannot mark all notifications as read, try again later!");
        return;
      }
      this.notifications = this.notifications.map(n => ({ ...n, unread: false }));
      this.unreadCount = 0;
    });

    // const unreadIds = this.notifications.filter(n => n.unread).map(n => n.id);
    // if (unreadIds.length === 0) return;

    // // Optimistic update
    // this.notifications = this.notifications.map(n => ({ ...n, unread: false }));
    // this.unreadCount = 0;

    // this.notificationService.markAsRead(unreadIds).subscribe(res => {
    //   if (!res || !res.success) {
    //     this.notificationService.getNotifications().subscribe(n => this.applyNotifications(n));
    //   }
    // });
  }

  openNotification(n: NotificationItem): void {
    // Example: navigate to the post
    this.dropdownOpen = false;
    this.router.navigate(['/posts', n.postId]);

    // mark this single notification read locally + server
    if (n.unread) {
      n.unread = false;
      this.unreadCount = Math.max(0, this.unreadCount - 1);
      this.notificationService.markAsRead(n.id).subscribe(res => {
        if (!res || !res.success) {
          // on failure re-sync
          this.lastNotifId = 0;
          this.loadNotifs();
          this.unreadCount = this.notifications.filter(n => n.unread).length;
        }
      });
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    this.stopPolling();
  }

  markRead(id: number): void {

    this.notificationService.markAsRead(id).subscribe(res => {
      if (!res || !res.success) {
        this.myAlert.showAlert("Error", "Cannot mark notification as read, try again later!");
        return;
      }
      this.notifications.forEach(n => {
        if (n.id == id) n.unread = false;
      });
      this.unreadCount = this.unreadCount - 1;
      this.myAlert.showAlert("Success", "");
    });
  }

  markUnRead(id: number): void {

    this.notificationService.markAsUnRead(id).subscribe(res => {
      if (!res || !res.success) {
        this.myAlert.showAlert("Error", "Cannot mark notification as read, try again later!");
        return;
      }
      this.notifications.forEach(n => {
        if (n.id == id) n.unread = true;
      });
      this.unreadCount = this.unreadCount + 1;
      this.myAlert.showAlert("Success", "");
    });
  }

  loadNotifs() {
    console.log("last ID", this.lastNotifId)
    if (this.loadingMore || this.allLoaded) return;
    this.loadingMore = true;

    setTimeout(() => {

      this.notificationService.getNotifications(this.lastNotifId, this.pageSize)
        .pipe(catchError(err => {
          console.error('Polling error', err);
          return of([] as NotificationItem[]);
        })).subscribe({
          next: (data) => {
            if (data.length == 0) {
              this.allLoaded = true;
              this.loadingMore = false;
              return;
            }
            this.notifications = [...this.notifications, ...data];
            const oldestNotif = this.notifications.reduce((min, p) => p.id < min.id ? p : min, this.notifications[0]);
            this.lastNotifId = oldestNotif.id;
            this.unreadCount = this.notifications.filter(n => n.unread).length;

          },
          error: (err) => {
            console.error('Error fetching notifications:', err);
            // this.loading = false;
          },
          complete: () => {
            this.loadingMore = false;
          }
        });
    }, 1000);
  }

  onScroll(event: Event) {
    clearTimeout(this.scrollTimer);

    this.scrollTimer = setTimeout(() => {
      const element = event.target as HTMLElement;

      const scrollTop = element.scrollTop;
      const visibleHeight = element.clientHeight;
      const fullHeight = element.scrollHeight;

      if (scrollTop + visibleHeight >= fullHeight - 50) {
        if (!this.loadingMore && !this.allLoaded) {
          this.loadNotifs();
        }
      }
    }, 500);
  }
  trapScroll(e: WheelEvent) {
    const box = e.currentTarget as HTMLElement;

    const atTop = box.scrollTop === 0;
    const atBottom = box.scrollHeight - box.scrollTop === box.clientHeight;

    const scrollTop = box.scrollTop;
    const visibleHeight = box.clientHeight;
    const fullHeight = box.scrollHeight;

    if (scrollTop + visibleHeight >= fullHeight - 20) {
      this.loadNotifs();
    }

    if (atTop && e.deltaY < 0) {
      e.preventDefault();
    }
    if (atBottom && e.deltaY > 0) {
      e.preventDefault();
    }
  }
}