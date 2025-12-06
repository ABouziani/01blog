import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HeaderComponent } from './header/header';
import { RouterModule } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { FooterComponent } from './footer/footer';
import { AuthService } from './services/auth';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet,HeaderComponent,RouterModule,HttpClientModule, FooterComponent],
  templateUrl: './app.html',
  styleUrls: ['./app.css','./search/search.css']
})
export class AppComponent {
  protected readonly title = signal('01blog-frontend');
  constructor(private authService: AuthService) {}

ngOnInit() {
  this.authService.checkAuthStatus();
}
}
