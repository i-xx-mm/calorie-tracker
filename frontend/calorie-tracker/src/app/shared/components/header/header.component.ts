import { Component, OnInit, HostListener, ViewChild, ElementRef } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
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
  @ViewChild('avatarDropdownRef') avatarDropdownRef!: ElementRef<HTMLElement>;
  currentUser$ = this.authService.currentUser$;
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
    // Load saved avatar color from localStorage
    const savedColor = localStorage.getItem('avatarColor');
    if (savedColor && this.avatarColors.includes(savedColor)) {
      this.avatarColor = savedColor;
    }
  }

  toggleDropdown(event: MouseEvent): void {
    event.stopPropagation();
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
    if (!this.avatarDropdownRef) return;
    const target = event.target as Node;
    const isClickInside = this.avatarDropdownRef.nativeElement.contains(target);
    if (!isClickInside) {
      this.closeDropdown();
    }
  }
}