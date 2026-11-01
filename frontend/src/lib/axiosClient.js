import axios from 'axios';
import { tokenStorage } from './tokenStorage.js';

const baseURL = import.meta.env.VITE_API_BASE_URL || '/api/v1';

export const axiosClient = axios.create({
  baseURL,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
  },
});

axiosClient.interceptors.request.use((config) => {
  const token = tokenStorage.getAccessToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

let refreshPromise = null;

axiosClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const original = error.config || {};
    const isLoginRequest = original.url?.endsWith('/auth/login');
    const isRefreshRequest = original.url?.endsWith('/auth/refresh-token');
    if (error.response?.status === 401 && original.url && !original._retry && !isLoginRequest && !isRefreshRequest) {
      original._retry = true;
      refreshPromise ||= axios.post(`${baseURL}/auth/refresh-token`, null, { withCredentials: true });
      try {
        const response = await refreshPromise;
        refreshPromise = null;
        tokenStorage.setTokens(response.data.data);
        original.headers.Authorization = `Bearer ${response.data.data.accessToken}`;
        return axiosClient(original);
      } catch (refreshError) {
        refreshPromise = null;
        tokenStorage.clear();
        window.location.assign('/login');
        return Promise.reject(refreshError);
      }
    }
    return Promise.reject(error);
  },
);
