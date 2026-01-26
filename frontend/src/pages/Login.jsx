import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { LogIn, UserPlus, Lock, User, Mail, BadgeCheck, Loader2, Languages } from 'lucide-react';
import { login, register } from '../api/authApi';
import { useAuth } from '../context/AuthContext';
import { useI18n } from '../context/LanguageContext'; 
import { toast } from 'sonner';

const Login = () => {
  const { t, lang, changeLanguage } = useI18n(); 
  const [isLogin, setIsLogin] = useState(true);
  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState({ 
    username: '', 
    password: '',
    email: '',
    fullName: ''
  });
  
  const { loginUser } = useAuth();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      let data;
      if (isLogin) {
        data = await login(formData.username, formData.password);
        toast.success(`Welcome, ${data.username}`);
      } else {
        data = await register(formData);
        toast.success("Account created successfully");
      }
      loginUser(data);
    } catch (err) {
      toast.error(t('error.internal.server') || "Error");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center p-4 md:p-6 relative overflow-hidden bg-[#0a0510]">
      
      {/* Selector de Idioma Flotante - Ajustado para no chocar en móvil */}
      <div className="absolute top-4 right-4 md:top-8 md:right-8 z-50">
        <div className="flex items-center gap-2 bg-white/5 px-3 py-1.5 md:px-4 md:py-2 rounded-2xl border border-white/10 backdrop-blur-md">
          <Languages size={14} className="text-purple-400" />
          <select
            value={lang}
            onChange={(e) => changeLanguage(e.target.value)}
            className="bg-transparent text-[10px] md:text-xs outline-none font-black cursor-pointer appearance-none uppercase tracking-tighter text-white"
          >
            <option value="es" className="bg-[#130b1d]">ES</option>
            <option value="en" className="bg-[#130b1d]">EN</option>
            <option value="pt" className="bg-[#130b1d]">PT</option>
          </select>
        </div>
      </div>

      {/* Glow de fondo - Ajustado tamaño para que no se coma la pantalla en PC */}
      <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-75 md:w-125 h-75 md:h-125 bg-purple-600/10 blur-[80px] md:blur-[120px] rounded-full z-0" />

      <motion.div 
        initial={{ opacity: 0, scale: 0.95 }}
        animate={{ opacity: 1, scale: 1 }}
        className="bg-[#130b1d]/80 backdrop-blur-xl p-6 md:p-8 rounded-4xl md:rounded-[2.5rem] border border-white/10 w-full max-w-100 shadow-2xl z-10"
      >
        <div className="text-center mb-6 md:mb-8">
          <motion.div 
            key={isLogin ? 'login-icon' : 'reg-icon'}
            initial={{ scale: 0 }} animate={{ scale: 1 }}
            className="w-12 h-12 md:w-16 md:h-16 bg-linear-to-br from-purple-600 to-blue-600 rounded-xl md:rounded-2xl mx-auto mb-4 flex items-center justify-center shadow-lg shadow-purple-500/20"
          >
            {isLogin ? <LogIn size={24} className="text-white md:size-32" /> : <UserPlus size={24} className="text-white md:size-32" />}
          </motion.div>
          
          <h2 className="text-2xl md:text-3xl font-black bg-linear-to-r from-white to-gray-400 bg-clip-text text-transparent tracking-tighter uppercase">
            {isLogin ? t('login.title') : t('login.register')}
          </h2>
          <p className="text-purple-400 text-[9px] md:text-[10px] font-mono tracking-[0.3em] mt-2 uppercase">
            {t('login.welcome_layer')}
          </p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-3 md:space-y-4">
          <AnimatePresence mode="popLayout">
            {!isLogin && (
              <motion.div
                initial={{ opacity: 0, height: 0 }}
                animate={{ opacity: 1, height: 'auto' }}
                exit={{ opacity: 0, height: 0 }}
                className="space-y-3 md:space-y-4 overflow-hidden"
              >
                <div className="relative group">
                  <BadgeCheck className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-500 group-focus-within:text-purple-400 transition-colors" size={18} />
                  <input 
                    type="text" 
                    required
                    placeholder={t('placeholder.fullname')}
                    className="w-full bg-white/5 border border-white/10 rounded-xl md:rounded-2xl py-3 md:py-4 pl-12 pr-4 text-sm md:text-base text-white focus:outline-none focus:border-purple-500/50 transition-all placeholder:text-gray-600"
                    onChange={(e) => setFormData({...formData, fullName: e.target.value})}
                  />
                </div>
                <div className="relative group">
                  <Mail className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-500 group-focus-within:text-purple-400 transition-colors" size={18} />
                  <input 
                    type="email" 
                    required
                    placeholder={t('placeholder.email')}
                    className="w-full bg-white/5 border border-white/10 rounded-xl md:rounded-2xl py-3 md:py-4 pl-12 pr-4 text-sm md:text-base text-white focus:outline-none focus:border-purple-500/50 transition-all placeholder:text-gray-600"
                    onChange={(e) => setFormData({...formData, email: e.target.value})}
                  />
                </div>
              </motion.div>
            )}
          </AnimatePresence>

          <div className="relative group">
            <User className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-500 group-focus-within:text-purple-400 transition-colors" size={18} />
            <input 
              type="text" 
              required
              placeholder={t('placeholder.username')}
              className="w-full bg-white/5 border border-white/10 rounded-xl md:rounded-2xl py-3 md:py-4 pl-12 pr-4 text-sm md:text-base text-white focus:outline-none focus:border-purple-500/50 transition-all placeholder:text-gray-600"
              onChange={(e) => setFormData({...formData, username: e.target.value})}
            />
          </div>

          <div className="relative group">
            <Lock className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-500 group-focus-within:text-purple-400 transition-colors" size={18} />
            <input 
              type="password" 
              required
              placeholder={t('placeholder.password')}
              className="w-full bg-white/5 border border-white/10 rounded-xl md:rounded-2xl py-3 md:py-4 pl-12 pr-4 text-sm md:text-base text-white focus:outline-none focus:border-purple-500/50 transition-all placeholder:text-gray-600"
              onChange={(e) => setFormData({...formData, password: e.target.value})}
            />
          </div>

          <button 
            disabled={loading}
            className="w-full bg-linear-to-r from-purple-600 to-blue-600 hover:from-purple-500 hover:to-blue-500 text-white font-black py-3 md:py-4 rounded-xl md:rounded-2xl shadow-lg shadow-purple-500/20 transition-all active:scale-95 disabled:opacity-50 flex items-center justify-center gap-2 mt-2"
          >
            {loading ? (
              <Loader2 className="animate-spin" size={20} />
            ) : (
              <>
                {isLogin ? <LogIn size={18} /> : <UserPlus size={18} />}
                <span className="text-xs md:text-sm tracking-widest">{isLogin ? t('login.btn') : t('login.btn_reg')}</span>
              </>
            )}
          </button>
        </form>

        <div className="text-center mt-6 md:mt-8">
          <button 
            onClick={() => setIsLogin(!isLogin)}
            className="text-gray-500 text-[9px] md:text-[10px] font-bold hover:text-white transition-colors uppercase tracking-[0.2em]"
          >
            {isLogin ? t('login.switch_reg') : t('login.switch_log')}
          </button>
        </div>
      </motion.div>
    </div>
  );
};

export default Login;