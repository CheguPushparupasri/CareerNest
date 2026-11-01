import { axiosClient } from '../../../lib/axiosClient.js';

export const notificationService = {
  getAll: async () => (await axiosClient.get('/notifications')).data.data,
  getUnread: async () => (await axiosClient.get('/notifications/unread')).data.data,
  getCount: async () => (await axiosClient.get('/notifications/count')).data.data,
  markAsRead: async (id) => (await axiosClient.put(`/notifications/${id}/read`)).data,
  markAllAsRead: async () => (await axiosClient.put('/notifications/read-all')).data,
  delete: async (id) => (await axiosClient.delete(`/notifications/${id}`)).data,
};