import { Injectable, OnDestroy } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

/**
 * Notification model for toast alert messages
 */
export interface Notification {
  /** Display text content of notification */
  message: string;
  /** Severity type controls styling: success/error/info/warning */
  type: 'success' | 'error' | 'info' | 'warning';
  /** Unique identifier for each notification instance */
  id: string;
  /** Auto-dismiss time in milliseconds; skip auto-close if zero */
  duration?: number;
}

/**
 * Global notification service managing toast message state
 * Uses BehaviorSubject to hold notification array, provides typed helper methods for each alert type
 * Tracks timeout ids to avoid setTimeout memory leak
 * Supports auto-dismiss via setTimeout and manual removal/clear-all operations
 */
@Injectable({
  providedIn: 'root',
})
export class NotificationService implements OnDestroy {
  /** Internal subject holding reactive list of active notifications */
  private notifications$ = new BehaviorSubject<Notification[]>([]);

  /** Track pending auto-dismiss timeout ids to prevent memory leak */
  private readonly timeoutMap = new Map<
    string,
    ReturnType<typeof setTimeout>
  >();

  /**
   * Get read-only observable stream for notification list
   * @returns Observable of active Notification array
   */
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

  /**
   * Private helper: create notification object, append to subject stream
   * Schedules auto‑removal setTimeout when duration > 0, store timer id for cleanup
   * Uses timestamp string as unique notification id
   *
   * @param message display message
   * @param type notification severity type
   * @param duration auto dismiss delay in milliseconds
   */
  private addNotification(
    message: string,
    type: Notification['type'],
    duration: number,
  ): void {
    const notification: Notification = {
      message,
      type,
      id: Date.now().toString(),
      duration,
    };

    const current = this.notifications$.value;
    this.notifications$.next([...current, notification]);

    if (duration > 0) {
      const timerId = setTimeout(() => this.remove(notification.id), duration);
      this.timeoutMap.set(notification.id, timerId);
    }
  }

  /**
   * Remove single notification by id
   *
   * @param id target notification unique id
   */
  remove(id: string): void {
    // clear pending timer before removing from stream
    const timerId = this.timeoutMap.get(id);
    if (timerId) {
      clearTimeout(timerId);
      this.timeoutMap.delete(id);
    }
    const current = this.notifications$.value;
    this.notifications$.next(current.filter((n) => n.id !== id));
  }

  /**
   * Public alias for removing single notification, called from UI toast component
   *
   * @param id target notification unique id
   */
  removeNotification(id: string): void {
    this.remove(id);
  }

  /**
   * Clear all active notifications at once and cancel all pending auto‑dismiss timers
   */
  clearAll(): void {
    // cleanup every pending timeout
    for (const timerId of this.timeoutMap.values()) {
      clearTimeout(timerId);
    }
    this.timeoutMap.clear();
    this.notifications$.next([]);
  }

  /**
   * On service destroy: clean up all remaining timers to prevent memory leak
   */
  ngOnDestroy(): void {
    for (const timerId of this.timeoutMap.values()) {
      clearTimeout(timerId);
    }
    this.timeoutMap.clear();
    this.notifications$.complete();
  }
}
