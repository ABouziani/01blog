import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-admin-summary',
  templateUrl: './admin-summary-component.html',
  styleUrls: ['./admin-summary-component.css']
})
export class AdminSummaryComponent implements OnInit {

  apiUrl = 'http://localhost:8080/api/admin/summary';

  constructor(private http: HttpClient) {}   // ✅ Correct injection

  ngOnInit() {
    this.http.get<any>(this.apiUrl).subscribe((resp) => {
      console.log(resp);
    });
  }
}
