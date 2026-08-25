import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

/**
 * Route guard for protected routes
 * Blocks unauthenticated users, redirects to login page when user is not logged in
 * Allows navigation if authService reports user is authenticated.
 * 
 * @param route target route being activated
 * @param state router state for the navigation
 * @returns true when allowed to proceed, false to block navigation
 */
export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isAuthenticated()) {
    return true;
  }

  router.navigate(['/auth/login']);
  return false;
};

/**
 * Route guard for public-only pages (login/register)
 * Authenticated users cannot access these pages, redirect to dashboard.
 * Allows navigation only when user is NOT logged in
 * 
 * @param route target route being activated
 * @param state router state for the navigation
 * @returns true when allowed to proceed, false to block navigation
 */
export const publicGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.isAuthenticated()) {
    return true;
  }

  router.navigate(['/dashboard']);
  return false;
};