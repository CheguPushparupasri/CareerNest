export function Button({ className = '', variant = 'primary', ...props }) {
  const variants = {
    primary: 'bg-gradient-to-r from-brand-600 to-violet-600 text-white shadow-glow hover:brightness-110',
    secondary: 'bg-slate-100 text-slate-900 hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-100 dark:hover:bg-slate-700',
    danger: 'bg-red-600 text-white hover:bg-red-700',
  };
  return (
    <button
      className={`inline-flex min-h-11 items-center justify-center rounded-xl px-4 py-2 text-sm font-semibold transition-all duration-200 disabled:cursor-not-allowed disabled:opacity-60 ${variants[variant]} ${className}`}
      {...props}
    />
  );
}
