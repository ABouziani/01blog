import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HeaderComponent } from './header/header';
import { RouterModule } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { FooterComponent } from './footer/footer';
import { AuthService } from './services/auth';
import { SearchBarComponent } from './search/search';
import { SearchResultsComponent } from './search-results/search-results';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet,HeaderComponent,RouterModule,HttpClientModule, FooterComponent, SearchBarComponent, SearchResultsComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class AppComponent {
  protected readonly title = signal('01blog-frontend');
  constructor(private authService: AuthService) {}

ngOnInit() {
  this.authService.checkAuthStatus();
}
}
