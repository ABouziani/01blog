import { Component } from '@angular/core';
import { PostsComponent } from '../posts/posts';

@Component({
  selector: 'app-home',
  imports: [PostsComponent],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class HomeComponent {

}
