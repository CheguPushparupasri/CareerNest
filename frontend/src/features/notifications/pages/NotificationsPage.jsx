import { useCallback, useEffect, useState } from 'react';
import { Bell, Check, CheckCheck, Trash2 } from 'lucide-react';
import { Alert } from '../../../components/ui/Alert.jsx';
import { Button } from '../../../components/ui/Button.jsx';
import { Panel } from '../../../components/layout/ui/Panel.jsx';
import { notificationService } from '../services/notificationService.js';

export function NotificationsPage() {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [message, setMessage] = useState('');

  const loadNotifications = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      setItems(await notificationService.getAll());
    } catch (requestError) {
      setError(requestError.response?.data?.message || 'Unable to load notifications.');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { loadNotifications(); }, [loadNotifications]);

  const markRead = async (item) => {
    try {
      await notificationService.markAsRead(item.id);
      setItems((current) => current.map((notification) => notification.id === item.id
        ? { ...notification, isRead: true }
        : notification));
      setMessage('Notification marked as read.');
    } catch (requestError) {
      setError(requestError.response?.data?.message || 'Unable to update notification.');
    }
  };

  const markAllRead = async () => {
    try {
      await notificationService.markAllAsRead();
      setItems((current) => current.map((item) => ({ ...item, isRead: true })));
      setMessage('All notifications marked as read.');
    } catch (requestError) {
      setError(requestError.response?.data?.message || 'Unable to update notifications.');
    }
  };

  const remove = async (item) => {
    if (!window.confirm(`Delete notification "${item.title}"?`)) return;
    try {
      await notificationService.delete(item.id);
      setItems((current) => current.filter((notification) => notification.id !== item.id));
      setMessage('Notification deleted.');
    } catch (requestError) {
      setError(requestError.response?.data?.message || 'Unable to delete notification.');
    }
  };

  const unreadCount = items.filter((item) => !item.isRead).length;

  return (
    <section className="space-y-6">
      <header className="flex flex-col gap-4 sm:flex-row sm:items-end sm:justify-between">
        <div>
          <h1 className="text-2xl font-semibold text-slate-950 dark:text-slate-50">Notifications</h1>
          <p className="mt-1 text-sm text-slate-600 dark:text-slate-400">{unreadCount} unread</p>
        </div>
        <Button type="button" variant="secondary" disabled={unreadCount === 0} onClick={markAllRead}>
          <CheckCheck size={17} className="mr-2" /> Mark all read
        </Button>
      </header>
      {error && <Alert type="error">{error}</Alert>}
      {message && <Alert type="success">{message}</Alert>}
      {loading ? (
        <div className="space-y-3" aria-label="Loading notifications">
          {[1, 2, 3].map((item) => <div key={item} className="h-24 animate-pulse rounded-xl bg-slate-200 dark:bg-slate-800" />)}
        </div>
      ) : items.length === 0 ? (
        <Panel className="flex flex-col items-center gap-3 p-10 text-center">
          <Bell size={24} className="text-slate-400" aria-hidden="true" />
          <p className="font-medium text-slate-900 dark:text-slate-100">No notifications yet</p>
          <p className="text-sm text-slate-500">CareerNest activity notifications will appear here.</p>
        </Panel>
      ) : (
        <div className="space-y-3">
          {items.map((item) => (
            <Panel key={item.id} className={`p-4 ${item.isRead ? 'opacity-75' : 'border-l-4 border-l-brand-600'}`}>
              <div className="flex items-start gap-3">
                <Bell size={18} className="mt-1 shrink-0 text-brand-600" aria-hidden="true" />
                <div className="min-w-0 flex-1">
                  <div className="flex flex-wrap items-center gap-2">
                    <h2 className="font-semibold text-slate-950 dark:text-slate-50">{item.title}</h2>
                    {!item.isRead && <span className="rounded-full bg-brand-50 px-2 py-0.5 text-xs text-brand-700 dark:bg-brand-950/30 dark:text-brand-300">Unread</span>}
                    <span className="rounded-full bg-slate-100 px-2 py-0.5 text-xs text-slate-600 dark:bg-slate-800 dark:text-slate-300">{item.type}</span>
                  </div>
                  <p className="mt-1 text-sm text-slate-600 dark:text-slate-400">{item.message}</p>
                  <p className="mt-2 text-xs text-slate-500">{item.createdAt ? new Date(item.createdAt).toLocaleString() : ''}</p>
                </div>
                <div className="flex shrink-0 gap-2">
                  {!item.isRead && <button type="button" title="Mark as read" aria-label="Mark as read" onClick={() => markRead(item)} className="rounded-lg p-2 text-slate-600 hover:bg-slate-100 dark:text-slate-300 dark:hover:bg-slate-800"><Check size={17} /></button>}
                  <button type="button" title="Delete notification" aria-label="Delete notification" onClick={() => remove(item)} className="rounded-lg p-2 text-red-600 hover:bg-red-50 dark:hover:bg-red-950/30"><Trash2 size={17} /></button>
                </div>
              </div>
            </Panel>
          ))}
        </div>
      )}
    </section>
  );
}