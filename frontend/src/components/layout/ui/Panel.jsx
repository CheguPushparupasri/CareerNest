import { motion } from 'framer-motion';

export function Panel({ children, className = '', hover = false }) {
  return (
    <motion.div
      initial={{ opacity: 0, y: 14 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.24, ease: 'easeOut' }}
      className={`rounded-2xl border border-slate-200/80 bg-white/90 p-5 shadow-[0_20px_50px_-20px_rgba(15,23,42,0.25)] backdrop-blur dark:border-slate-800/80 dark:bg-slate-900/80 ${hover ? 'transition-transform duration-200 hover:-translate-y-1' : ''} ${className}`}
    >
      {children}
    </motion.div>
  );
}
