import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';

/**
 * Angular HTTP interceptor to attach JWT Bearer token onto outgoing API requests
 * Skips adding authorization header for authentication endpoints under '/auth/'
 * to avoid attaching token to login/register requests.
 */
@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {}

  /**
   * Intercept every outgoing HTTP request
   * Reads stored JWT token from AuthService, injects Authorization Bearer header if token exists
   * and request is not targeting auth routes
   *
   * @param req original outgoing http request
   * @param next http handler to pass cloned/modified request down the interceptor chain
   * @returns Observable of HttpEvent stream
   */
  intercept(
    req: HttpRequest<unknown>,
    next: HttpHandler,
  ): Observable<HttpEvent<unknown>> {
    const token = this.authService.getToken();

    if (token && !req.url.includes('/auth/')) {
      req = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`,
        },
      });
    }

    return next.handle(req);
  }
}
