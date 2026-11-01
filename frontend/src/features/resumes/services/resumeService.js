import { axiosClient } from '../../../lib/axiosClient.js';

export const resumeService = {
  getAll: async () => (await axiosClient.get('/resumes')).data.data,
  getById: async (id) => (await axiosClient.get(`/resumes/${id}`)).data.data,
  getPrimary: async () => (await axiosClient.get('/resumes/primary')).data.data,
  create: async (data) => (await axiosClient.post('/resumes', data)).data.data,
  update: async (id, data) => (await axiosClient.put(`/resumes/${id}`, data)).data.data,
  delete: async (id) => axiosClient.delete(`/resumes/${id}`),
  setPrimary: async (id) => axiosClient.patch(`/resumes/${id}/primary`),
  getVersions: async (id) => (await axiosClient.get(`/resumes/${id}/versions`)).data.data,
  exportToPdf: async (id) => (await axiosClient.get(`/resumes/${id}/export`)).data.data,
};
