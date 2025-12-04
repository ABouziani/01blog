import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { SearchBarComponent } from '../search/search';

interface recentPosts { id:number; title: string; creatorId: number; creatorUsername: string; media: string; }
interface activeUsers { id:number; name: string; posts: number; avatar: string; }
interface topPosts { id:number; title: string; creatorId: number; creatorUsername: string; media: string; }


@Component({
  selector: 'app-home',
  imports: [CommonModule, SearchBarComponent],
  standalone: true,
  templateUrl: './home.html',
  styleUrls: ['./home.css']
})
export class HomeComponent {
  apiUrl = 'http://localhost:8080/api/home';
  constructor(private http: HttpClient, public router:Router) { }
  recentPosts: recentPosts[] = [];
  activeUsers: activeUsers[] = [];
  topPosts: topPosts[] = [];

  trendingTags = [
    { label: '#ComputerScience', primary: true },
    { label: '#StudyAbroad', primary: false },
    { label: '#Internships', primary: false },
    { label: '#WebDevelopment', primary: true },
    { label: '#FinalsWeek', primary: false },
    { label: '#AI', primary: false },
    { label: '#BioTech', primary: false },
    { label: '#Hackathon', primary: true },
  ];


  ngOnInit(): void {

    this.http.get<{ recentPosts: recentPosts[], activeUsers: activeUsers[], topPosts:topPosts[] }>(this.apiUrl).subscribe({
      next: (resp) => {
        console.log(resp)
        this.recentPosts = resp.recentPosts;
        this.activeUsers = resp.activeUsers;
        this.topPosts = resp.topPosts;
      },
      error: (err) => {
        console.error('Error fetching Home data:', err);
      }
    });
  }
}