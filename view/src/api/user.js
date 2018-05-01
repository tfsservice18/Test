/*const body = {
  host: 'lightapi.net',
  service: 'user',
  action: 'loginUser',
  version: '0.1.0',
  data: {nameOrEmail:'steve', password: '11111111' },
};*/

/**
 * Returns a promise from Menu Service
 * @returns Promise
 */
export default async (body) => {
  const res = await fetch('/api/json', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(body),
  });
  return res.json();
};
