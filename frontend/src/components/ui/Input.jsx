export function Input({ label, error, className = '', ...props }) {
  return (
    <label className="block space-y-1.5">
      <span className="text-sm font-medium text-slate-700 dark:text-slate-200">{label}</span>
      <input
        className={`min-h-11 w-full rounded-lg border border-slate-300 bg-white px-3 text-sm outline-none transition focus:border-brand-600 focus:ring-4 focus:ring-blue-100 dark:border-slate-700 dark:bg-slate-900 dark:text-slate-100 dark:focus:ring-blue-950 ${className}`}
        {...props}
      />
      {error ? <span className="text-sm text-red-600">{error}</span> : null}
    </label>
  );
}
