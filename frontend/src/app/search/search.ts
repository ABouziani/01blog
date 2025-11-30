import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { Router } from '@angular/router';

@Component({
  selector: 'app-search-bar',
  standalone : true,
  imports: [
    CommonModule,
    FormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule
  ],
  templateUrl: './search.html',
  styleUrl: './search.css'
})
export class SearchBarComponent {
query = '';
  type: 'users' | 'posts' = 'users';

  constructor(private router: Router) {}

  onSearch(): void {
    if (this.query.trim()) {
      // ✅ Navigate to /searchresults with query params
      this.router.navigate(['/searchresults'], {
        queryParams: {
          type: this.type,
          q: this.query.trim()
        },
        // Optional: replace history (no back to same search)
        // replaceUrl: true
      });
    }
  }
}
