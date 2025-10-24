// app/admin/admin-panel.component.ts
import { Component, inject } from '@angular/core';
import { AsyncPipe } from '@angular/common';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { RouterModule } from '@angular/router';
import { ActiveTabService } from '../services/active-tab';
import { AdminUsersComponent } from '../users/users';
import { AdminPostsComponent } from '../posts/posts';
import { AdminReportsComponent } from '../reports/reports';

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
    AdminReportsComponent
  ],
  templateUrl: './adminpanel.html',
  styleUrl: './adminpanel.css'
})
export class AdminPanel {
  activeTab = inject(ActiveTabService).activeTab;
  selectTab(tab: 'users' | 'posts' | 'reports') {
    this.activeTab.set(tab);
  }
}