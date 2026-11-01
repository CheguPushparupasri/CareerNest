let accessToken = null;

export const tokenStorage = {
  getAccessToken: () => accessToken,
  setTokens: (tokens) => { accessToken = tokens.accessToken; },
  clear: () => { accessToken = null; },
};
