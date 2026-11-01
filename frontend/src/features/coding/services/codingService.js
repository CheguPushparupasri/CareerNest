import { axiosClient } from '../../../lib/axiosClient.js';

export const codingService = {
  getAll: async (params = {}) => (await axiosClient.get('/coding-profiles', { params })).data.data,
  getById: async (id) => (await axiosClient.get(`/coding-profiles/${id}`)).data.data,
  create: async (data) => (await axiosClient.post('/coding-profiles', data)).data.data,
  update: async (id, data) => (await axiosClient.put(`/coding-profiles/${id}`, data)).data.data,
  delete: async (id) => axiosClient.delete(`/coding-profiles/${id}`),
  search: async (query, params = {}) => (await axiosClient.get('/coding-profiles/search', { params: { ...params, query } })).data.data,
  getByPlatform: async (platform, params = {}) => (await axiosClient.get(`/coding-profiles/platform/${platform}`, { params })).data.data,
};
