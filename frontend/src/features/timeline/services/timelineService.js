import { axiosClient } from '../../../lib/axiosClient.js';

export const timelineService = {
  getAll: async (params = {}) => (await axiosClient.get('/timeline', { params })).data.data,
  getById: async (id) => (await axiosClient.get(`/timeline/${id}`)).data.data,
  create: async (data) => (await axiosClient.post('/timeline', data)).data.data,
  update: async (id, data) => (await axiosClient.put(`/timeline/${id}`, data)).data.data,
  delete: async (id) => axiosClient.delete(`/timeline/${id}`),
  search: async (query, params = {}) => (await axiosClient.get('/timeline/search', { params: { ...params, query } })).data.data,
  getByType: async (type, params = {}) => (await axiosClient.get(`/timeline/type/${type}`, { params })).data.data,
};
