// app/admin/admin-panel.component.ts
import { Component, inject } from '@angular/core';
import { AsyncPipe } from '@angular/common';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { ActiveTabService } from '../services/active-tab';
import { AdminUsersComponent } from '../users/users';
import { AdminPostsComponent } from '../posts/posts';
import { AdminReportsComponent } from '../reports/reports';
import { HttpClient } from '@angular/common/http';
import { AdminSummaryComponent } from '../admin-summary-component/admin-summary-component';

@Component({
  selector: 'app-admin-panel',
  standalone: true,
  imports: [
    MatToolbarModule,
    MatSidenavModule,
    MatListModule,
    MatButtonModule,
    // AsyncPipe,
    RouterModule,
    AdminUsersComponent,
    AdminPostsComponent,
    AdminReportsComponent,
    AdminSummaryComponent
  ],
  templateUrl: './adminpanel.html',
  styleUrl: './adminpanel.css'
})
export class AdminPanel {

  private apiUrl = 'http://localhost:8080/api/auth/islogged';
  adminUsername = '';

  activeTab = inject(ActiveTabService).activeTab;
  selectTab(tab: 'summary' | 'users' | 'posts' | 'reports') {
    this.activeTab.set(tab);
  }
  constructor(private http: HttpClient, private route: ActivatedRoute) { }

  ngOnInit() {
    this.http.get<{ username: string }>(`${this.apiUrl}`).subscribe((resp) => {
      this.adminUsername = resp.username.toUpperCase();
    });
    this.route.firstChild?.url.subscribe(urlSegments => {
      const tab = urlSegments[0]?.path as 'summary' | 'users' | 'posts' | 'reports';
      if (tab) this.activeTab.set(tab);
    });
  }
}