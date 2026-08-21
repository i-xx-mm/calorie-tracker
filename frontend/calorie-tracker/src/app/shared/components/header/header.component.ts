import { Component, OnInit, HostListener } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { User } from '../../models/user.model';
import { trigger, transition, style, animate } from '@angular/animations';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css'],
  animations: [
    trigger('slideDown', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(-10px)' }),
        animate('150ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ]),
      transition(':leave', [
        animate('150ms ease-in', style({ opacity: 0, transform: 'translateY(-10px)' }))
      ])
    ])
  ]
})
export class HeaderComponent implements OnInit {
  currentUser: User | null = null;
  showDropdown = false;
  
  avatarColors = [
    '#E8A8B8',
    '#D498AD',
    '#94B8D8',
    '#7FA8C8',
    '#C2B2D0',
    '#B0C8E0',
    '#D8B4C2',
    '#c9dbc9'
  ];
  avatarColor = '#94B8D8';

  constructor(private authService: AuthService, private router: Router) { }

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
      if(this.currentUser) {
        this.currentUser.username = this.currentUser.username ?? '';
      }
    });
    
    // Load saved avatar color from localStorage
    const savedColor = localStorage.getItem('avatarColor');
    if (savedColor && this.avatarColors.includes(savedColor)) {
      this.avatarColor = savedColor;
    }
  }

  toggleDropdown(): void {
    this.showDropdown = !this.showDropdown;
  }

  closeDropdown(): void {
    this.showDropdown = false;
  }

  changeAvatarColor(color: string): void {
    this.avatarColor = color;
    localStorage.setItem('avatarColor', color);
  }

  logout(): void {
    this.closeDropdown();
    this.authService.logout();
    this.router.navigate(['/auth/login']);
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    const target = event.target as HTMLElement;
    const headerElement = document.querySelector('app-header');
    if (!headerElement?.contains(target)) {
      this.closeDropdown();
    }
  }
}