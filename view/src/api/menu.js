const body = {
  host: 'lightapi.net',
  service: 'menu',
  action: 'getMenuByHost',
  version: '0.1.0',
  data: { host: 'lightapi.net' },
};

/**
 * Returns a promise from Menu Service
 * @returns Promise
 */
export default async () => {
  const res = await fetch('/api/query', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(body),
  });
  return res.json();
};
