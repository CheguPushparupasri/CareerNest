export function Alert({ children, type = 'info' }) {
  const styles = {
    info: 'border-blue-200 bg-blue-50 text-blue-800 dark:border-blue-900 dark:bg-blue-950 dark:text-blue-200',
    error: 'border-red-200 bg-red-50 text-red-800 dark:border-red-900 dark:bg-red-950 dark:text-red-200',
    success: 'border-green-200 bg-green-50 text-green-800 dark:border-green-900 dark:bg-green-950 dark:text-green-200',
  };
  return <div className={`rounded-lg border px-4 py-3 text-sm ${styles[type]}`}>{children}</div>;
}
