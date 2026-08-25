import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';
import { NotificationService } from '../services/notification.service';
import { Router } from '@angular/router';

/**
 * Global HTTP error interceptor
 * Catches backend HTTP error responses, handles status-specific business logic,
 * shows user-friendly toast notifications, and re-throws error for component-level consumption
 */
@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  constructor(
    private authService: AuthService,
    private notificationService: NotificationService,
    private router: Router
  ) { }

  /**
   * Intercept all http responses and catch error events from response stream
   * 
   * @param req outgoing http request
   * @param next handler to forward request through interceptor pipeline
   * @returns Observable of http events, errors will be caught and processed locally
   */
  intercept(req: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        let message = 'An error occurred';

        if (error.status === 401) {
          this.authService.logout();
          this.router.navigate(['/auth/login']);
          message = 'Your session has expired. Please log in again.';
        } else if (error.status === 403) {
          message = 'You do not have permission to access this resource.';
        } else if (error.status === 404) {
          message = 'Resource not found.';
        } else if (error.status === 409) {
          message = error.error?.message || 'This resource already exists.';
        } else if (error.status === 422) {
          message = 'Validation error. Please check your input.';
        } else if (error.status >= 500) {
          message = 'Server error. Please try again later.';
        } else if (error.error?.message) {
          message = error.error.message;
        }

        this.notificationService.error(message);
        return throwError(() => error);
      })
    );
  }
}