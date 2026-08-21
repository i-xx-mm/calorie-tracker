import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-root',
  template: `
    <app-header></app-header>
    <app-notification-container></app-notification-container>
    <main class="container">
      <router-outlet></router-outlet>
    </main>
  `,
  styles: [`
    main {
      padding: 2rem 0;
      min-height: calc(100vh - 60px);
    }
  `]
})
export class AppComponent implements OnInit {
  ngOnInit(): void {
    // Verify and clean localStorage on startup
    this.validateLocalStorage();
  }

  private validateLocalStorage(): void {
    try {
      const user = localStorage.getItem('currentUser');
      if (user) {
        JSON.parse(user);
      }
    } catch (error) {
      console.warn('Corrupted localStorage detected, clearing...');
      localStorage.clear();
    }
  }
}