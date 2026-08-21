import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

export interface Notification {
  message: string;
  type: 'success' | 'error' | 'info' | 'warning';
  id: string;
  duration?: number;
}

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  public notifications$ = new BehaviorSubject<Notification[]>([]);

  getNotifications(): Observable<Notification[]> {
    return this.notifications$.asObservable();
  }

  success(message: string, duration: number = 3000): void {
    this.addNotification(message, 'success', duration);
  }

  error(message: string, duration: number = 5000): void {
    this.addNotification(message, 'error', duration);
  }

  info(message: string, duration: number = 3000): void {
    this.addNotification(message, 'info', duration);
  }

  warning(message: string, duration: number = 4000): void {
    this.addNotification(message, 'warning', duration);
  }

  private addNotification(message: string, type: Notification['type'], duration: number): void {
    const notification: Notification = {
      message,
      type,
      id: Date.now().toString(),
      duration
    };

    const current = this.notifications$.value;
    this.notifications$.next([...current, notification]);

    if (duration > 0) {
      setTimeout(() => this.remove(notification.id), duration);
    }
  }

  remove(id: string): void {
    const current = this.notifications$.value;
    this.notifications$.next(current.filter(n => n.id !== id));
  }

  removeNotification(id: string): void {
    this.remove(id);
  }

  clearAll(): void {
    this.notifications$.next([]);
  }
}