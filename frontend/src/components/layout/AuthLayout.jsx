import { motion } from 'framer-motion';
import { Sparkles, ShieldCheck, Briefcase } from 'lucide-react';

export function AuthLayout({ title, subtitle, children }) {
  return (
    <main className="grid min-h-screen bg-background dark:bg-background-dark text-text dark:text-text-dark lg:grid-cols-[1.1fr_0.9fr]">
      <section className="hidden items-center bg-[radial-gradient(circle_at_top_left,_rgba(79,70,229,0.22),_transparent_38%),linear-gradient(135deg,_#0f172a_0%,_#111827_100%)] px-12 py-16 text-white lg:flex">
        <motion.div initial={{ opacity: 0, y: 16 }} animate={{ opacity: 1, y: 0 }} className="max-w-xl">
          <div className="flex items-center gap-3">
            <div className="flex h-11 w-11 items-center justify-center rounded-2xl bg-gradient-to-br from-primary-500 to-secondary-500 shadow-glow">
              <Briefcase size={20} />
            </div>
            <div>
              <p className="text-sm font-semibold uppercase tracking-[0.35em] text-accent-400">CareerNest</p>
              <p className="text-xs text-slate-300">Premium career operating system</p>
            </div>
          </div>
          <h1 className="mt-6 text-5xl font-semibold leading-tight">Build your career like a modern product team.</h1>
          <p className="mt-5 text-lg text-slate-300">Track projects, resumes, learning, internships, and placement readiness from one beautifully designed workspace.</p>
          <div className="mt-8 flex flex-wrap gap-3">
            {['Premium dashboard', 'Realtime insights', 'AI guidance'].map((item) => (
              <span key={item} className="rounded-full border border-white/10 bg-white/10 px-3 py-1.5 text-sm text-slate-100">{item}</span>
            ))}
          </div>
        </motion.div>
      </section>
      <section className="flex items-center justify-center px-5 py-10 sm:px-8">
        <motion.div initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }} className="w-full max-w-md rounded-3xl border border-border dark:border-borderDark bg-white/90 p-6 shadow-premium-lg backdrop-blur dark:bg-slate-900/80">
          <div className="mb-6">
            <div className="flex items-center gap-2 text-sm font-semibold text-primary-600 dark:text-primary-400">
              <ShieldCheck size={16} />
              Secure workspace
            </div>
            <h2 className="mt-3 text-2xl font-semibold text-text dark:text-text-dark">{title}</h2>
            <p className="mt-1 text-sm text-text-muted dark:text-text-mutedDark">{subtitle}</p>
          </div>
          {children}
        </motion.div>
      </section>
    </main>
  );
}
