import { axiosClient } from '../../../lib/axiosClient.js';

export const adminService = {
  getOverview: async () => (await axiosClient.get('/admin/overview')).data.data,
  getUsers: async (params = {}) => (await axiosClient.get('/admin/users', { params })).data.data,
  getAuditLogs: async (params = {}) => (await axiosClient.get('/admin/audit-logs', { params })).data.data,
  getHealth: async () => (await axiosClient.get('/health')).data.data,
};
