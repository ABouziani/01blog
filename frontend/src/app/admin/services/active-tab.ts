// app/admin/active-tab.service.ts
import { Injectable } from '@angular/core';
import { signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class ActiveTabService {
  activeTab = signal<'summary' | 'users' | 'posts' | 'reports'>('summary');
}