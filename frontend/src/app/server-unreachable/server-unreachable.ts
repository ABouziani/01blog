import { Component, EventEmitter, inject, Output } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-server-unreachable',
  imports: [],
  templateUrl: './server-unreachable.html',
  styleUrl: './server-unreachable.css'
})


export class ServerUnreachableComponent {
  @Output() retry = new EventEmitter<void>();

  public router = inject(Router);
  
  showRetry = true;
}

