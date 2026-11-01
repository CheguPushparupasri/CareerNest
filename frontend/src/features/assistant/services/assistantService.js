import { axiosClient } from '../../../lib/axiosClient.js';

export const assistantService = {
  advise: async (prompt) => (await axiosClient.post('/assistant', { prompt })).data.data,
};
