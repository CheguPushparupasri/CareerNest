import { useState, useRef, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Sparkles, Send, Bot, User, MoreVertical, Copy, Check, Lightbulb, Code, FileText, Briefcase, TrendingUp } from 'lucide-react';
import { assistantService } from '../services/assistantService.js';
import { Panel } from '../../../components/layout/ui/Panel.jsx';

export function AssistantPage() {
  const [messages, setMessages] = useState([
    {
      id: 1,
      role: 'assistant',
      content: "Hello! I'm your AI Career Assistant. I can help you with:\n\n• Resume building and optimization\n• Interview preparation\n• Career path planning\n• Skill development guidance\n• Project portfolio advice\n\nHow can I help you today?",
      timestamp: new Date(),
    },
  ]);
  const [input, setInput] = useState('');
  const [isTyping, setIsTyping] = useState(false);
  const [copiedId, setCopiedId] = useState(null);
  const messagesEndRef = useRef(null);
  const inputRef = useRef(null);

  const suggestedQuestions = [
    { icon: FileText, text: 'How can I improve my resume?' },
    { icon: Briefcase, text: 'What skills are in demand for software engineers?' },
    { icon: TrendingUp, text: 'How should I prepare for technical interviews?' },
    { icon: Lightbulb, text: 'What projects should I build to get hired?' },
  ];

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages, isTyping]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!input.trim() || isTyping) return;

    const userMessage = {
      id: Date.now(),
      role: 'user',
      content: input,
      timestamp: new Date(),
    };

    setMessages((prev) => [...prev, userMessage]);
    setInput('');
    setIsTyping(true);

    try {
      const data = await assistantService.advise(userMessage.content);
      const assistantMessage = {
        id: Date.now() + 1,
        role: 'assistant',
        content: data.summary || data.guidance || "I've analyzed your request. Here's my guidance based on your career goals.",
        timestamp: new Date(),
        metadata: {
          guidance: data.guidance,
          nextSteps: data.nextSteps,
        },
      };
      setMessages((prev) => [...prev, assistantMessage]);
    } catch (error) {
      const errorMessage = {
        id: Date.now() + 1,
        role: 'assistant',
        content: "I apologize, but I encountered an error processing your request. Please try again.",
        timestamp: new Date(),
      };
      setMessages((prev) => [...prev, errorMessage]);
    } finally {
      setIsTyping(false);
    }
  };

  const handleSuggestedQuestion = (question) => {
    setInput(question);
    inputRef.current?.focus();
  };

  const copyToClipboard = (text, id) => {
    navigator.clipboard.writeText(text);
    setCopiedId(id);
    setTimeout(() => setCopiedId(null), 2000);
  };

  return (
    <div className="flex h-[calc(100vh-8rem)] flex-col gap-4">
      {/* Header */}
      <Panel className="p-4">
        <div className="flex items-center gap-3">
          <div className="flex h-10 w-10 items-center justify-center rounded-2xl bg-gradient-to-br from-primary-500 to-secondary-500 text-white shadow-glow">
            <Sparkles size={20} />
          </div>
          <div>
            <h1 className="text-xl font-bold text-text dark:text-text-dark">AI Career Assistant</h1>
            <p className="text-xs text-text-muted dark:text-text-mutedDark">Powered by advanced AI for personalized guidance</p>
          </div>
        </div>
      </Panel>

      {/* Chat Area */}
      <div className="flex-1 overflow-y-auto rounded-2xl border border-border dark:border-borderDark bg-white dark:bg-slate-900 p-4">
        <div className="mx-auto max-w-3xl space-y-4">
          <AnimatePresence>
            {messages.map((message) => (
              <motion.div
                key={message.id}
                initial={{ opacity: 0, y: 10 }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0, y: -10 }}
                className={`flex gap-3 ${message.role === 'user' ? 'justify-end' : 'justify-start'}`}
              >
                {message.role === 'assistant' && (
                  <div className="flex h-8 w-8 shrink-0 items-center justify-center rounded-full bg-gradient-to-br from-primary-500 to-secondary-500 text-white">
                    <Bot size={16} />
                  </div>
                )}
                <div
                  className={`max-w-[80%] rounded-2xl p-4 ${
                    message.role === 'user'
                      ? 'bg-gradient-to-r from-primary-500 to-secondary-500 text-white'
                      : 'bg-slate-100 dark:bg-slate-800 text-text dark:text-text-dark'
                  }`}
                >
                  <div className="whitespace-pre-wrap text-sm leading-relaxed">{message.content}</div>
                  {message.metadata?.guidance && (
                    <div className="mt-3 space-y-2 border-t border-slate-200 dark:border-slate-700 pt-3">
                      <div className="text-xs">
                        <span className="font-semibold">Focus:</span> {message.metadata.guidance}
                      </div>
                      {message.metadata.nextSteps && (
                        <div className="text-xs">
                          <span className="font-semibold">Next steps:</span> {message.metadata.nextSteps}
                        </div>
                      )}
                    </div>
                  )}
                  <div className="mt-2 flex items-center justify-between gap-2">
                    <span className="text-xs opacity-60">
                      {message.timestamp.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                    </span>
                    <button
                      onClick={() => copyToClipboard(message.content, message.id)}
                      className="opacity-60 hover:opacity-100 transition-opacity"
                    >
                      {copiedId === message.id ? <Check size={14} /> : <Copy size={14} />}
                    </button>
                  </div>
                </div>
                {message.role === 'user' && (
                  <div className="flex h-8 w-8 shrink-0 items-center justify-center rounded-full bg-slate-200 dark:bg-slate-700 text-text dark:text-text-dark">
                    <User size={16} />
                  </div>
                )}
              </motion.div>
            ))}
          </AnimatePresence>

          {/* Typing Indicator */}
          {isTyping && (
            <motion.div
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              className="flex gap-3"
            >
              <div className="flex h-8 w-8 shrink-0 items-center justify-center rounded-full bg-gradient-to-br from-primary-500 to-secondary-500 text-white">
                <Bot size={16} />
              </div>
              <div className="rounded-2xl bg-slate-100 dark:bg-slate-800 p-4">
                <div className="flex gap-1">
                  <motion.div
                    animate={{ scale: [1, 1.2, 1] }}
                    transition={{ repeat: Infinity, duration: 0.8 }}
                    className="h-2 w-2 rounded-full bg-slate-400"
                  />
                  <motion.div
                    animate={{ scale: [1, 1.2, 1] }}
                    transition={{ repeat: Infinity, duration: 0.8, delay: 0.2 }}
                    className="h-2 w-2 rounded-full bg-slate-400"
                  />
                  <motion.div
                    animate={{ scale: [1, 1.2, 1] }}
                    transition={{ repeat: Infinity, duration: 0.8, delay: 0.4 }}
                    className="h-2 w-2 rounded-full bg-slate-400"
                  />
                </div>
              </div>
            </motion.div>
          )}
          <div ref={messagesEndRef} />
        </div>
      </div>

      {/* Suggested Questions */}
      {messages.length <= 1 && (
        <div className="mx-auto max-w-3xl">
          <div className="mb-3 text-xs font-medium uppercase tracking-wider text-text-muted dark:text-text-mutedDark">
            Suggested questions
          </div>
          <div className="flex flex-wrap gap-2">
            {suggestedQuestions.map((question, index) => {
              const Icon = question.icon;
              return (
                <button
                  key={index}
                  onClick={() => handleSuggestedQuestion(question.text)}
                  className="flex items-center gap-2 rounded-2xl border border-border dark:border-borderDark bg-white dark:bg-slate-900 px-4 py-2 text-sm text-text dark:text-text-dark hover:bg-slate-50 dark:hover:bg-slate-800 transition-colors"
                >
                  <Icon size={16} className="text-primary-600" />
                  {question.text}
                </button>
              );
            })}
          </div>
        </div>
      )}

      {/* Input Area */}
      <Panel className="p-4">
        <form onSubmit={handleSubmit} className="mx-auto max-w-3xl">
          <div className="flex gap-3">
            <div className="flex-1 relative">
              <input
                ref={inputRef}
                type="text"
                value={input}
                onChange={(e) => setInput(e.target.value)}
                placeholder="Ask anything about your career..."
                className="w-full rounded-2xl border border-border dark:border-borderDark bg-white dark:bg-slate-900 py-3 pl-4 pr-12 text-sm focus:outline-none focus:ring-2 focus:ring-primary-500"
                disabled={isTyping}
              />
              <button
                type="button"
                className="absolute right-2 top-1/2 -translate-y-1/2 rounded-xl p-2 text-text-muted hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors"
              >
                <MoreVertical size={18} />
              </button>
            </div>
            <button
              type="submit"
              disabled={!input.trim() || isTyping}
              className="flex h-12 w-12 items-center justify-center rounded-2xl bg-gradient-to-r from-primary-500 to-secondary-500 text-white shadow-glow hover:shadow-lg transition-all disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <Send size={20} />
            </button>
          </div>
        </form>
      </Panel>
    </div>
  );
}
