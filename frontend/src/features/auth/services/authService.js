import { axiosClient } from '../../../lib/axiosClient.js';

export const authService = {
  signup: async (payload) => (await axiosClient.post('/auth/signup', payload)).data,
  login: async (payload) => (await axiosClient.post('/auth/login', payload)).data.data,
  me: async () => (await axiosClient.get('/auth/me')).data.data,
  refresh: async () => (await axiosClient.post('/auth/refresh-token')).data.data,
  forgotPassword: async (payload) => (await axiosClient.post('/auth/forgot-password', payload)).data,
  resetPassword: async (payload) => (await axiosClient.post('/auth/reset-password', payload)).data,
  verifyEmail: async (token) => (await axiosClient.post('/auth/verify-email', { token })).data,
  resendVerification: async (email) => (await axiosClient.post('/auth/resend-verification', { email })).data,
  logout: async () => (await axiosClient.post('/auth/logout')).data,
  logoutAll: async () => (await axiosClient.post('/auth/logout-all')).data,
  sessions: async () => (await axiosClient.get('/auth/sessions')).data.data,
  revokeSession: async (sessionId) => (await axiosClient.delete(`/auth/sessions/${sessionId}`)).data,
};
