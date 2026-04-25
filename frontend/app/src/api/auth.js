const BASE_URL = 'http://localhost:8080/api';

const options = (method, body) => ({
  method,
  headers: { 'Content-Type': 'application/json' },
  credentials: 'include',
  body: body ? JSON.stringify(body) : undefined,
});

export const register = (username, password) =>
  fetch(`${BASE_URL}/register`, options('POST', { username, password })).then(r => r.json());

export const login = (username, password) =>
  fetch(`${BASE_URL}/login`, options('POST', { username, password })).then(r => r.json());

export const logout = () =>
  fetch(`${BASE_URL}/logout`, options('POST')).then(r => r.json());

export const me = () =>
  fetch(`${BASE_URL}/me`, options('GET')).then(r => r.json());

export const updateUsername = (username) =>
  fetch(`${BASE_URL}/update/username`, options('PUT', { username })).then(r => r.json());

export const updatePassword = (password) =>
  fetch(`${BASE_URL}/update/password`, options('PUT', { password })).then(r => r.json());

export const deleteAccount = () =>
  fetch(`${BASE_URL}/user`, options('DELETE')).then(r => r.json());