import { Component, inject, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ActiveTabService } from '../services/active-tab';

@Component({
  selector: 'app-admin-summary',
  templateUrl: './admin-summary-component.html',
  styleUrls: ['./admin-summary-component.css']
})
export class AdminSummaryComponent implements OnInit {

  apiUrl = 'http://localhost:8080/api/admin/summary';

  constructor(private http: HttpClient) { } 
  postsCounts: number = 0;
  postsReportsCount: number = 0;
  usersCounts: number = 0;
  usersReportsCount: number = 0;

  activeTab = inject(ActiveTabService).activeTab;
  selectTab(tab: 'summary' | 'users' | 'posts' | 'reports') {
    this.activeTab.set(tab);
  }

  ngOnInit(): void {
    this.http.get<any>(this.apiUrl).subscribe((resp) => {
      this.postsCounts = resp.postsCounts;
      this.postsReportsCount = resp.postsReportsCount;
      this.usersCounts = resp.usersCounts;
      this.usersReportsCount = resp.usersReportsCount;
    });

  }
}
