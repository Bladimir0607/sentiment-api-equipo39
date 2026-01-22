import React, { useEffect, useState } from 'react';
import { motion } from 'framer-motion';
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, Cell } from 'recharts';
import { getStats } from '../api/sentimentApi';
import { ArrowLeft, TrendingUp, Globe, Activity } from 'lucide-react';
import { useI18n } from '../context/LanguageContext';

const Stats = ({ onBack }) => {
  const { t } = useI18n();
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const response = await getStats();
        setData(response);
      } catch (error) {
        console.error("Error stats:", error);
      } finally {
        setLoading(false);
      }
    };
    fetchStats();
  }, []);

  const total = data?.total || 0;

  const chartData = [
    {
      name: t('stats.pos'), 
      value: data?.positive || 0, 
      color: '#10b981',
      percentage: total > 0 ? ((data?.positive / total) * 100).toFixed(1) : 0
    },
    { 
      name: t('stats.neu'), 
      value: data?.neutral || 0, 
      color: '#f59e0b',
      percentage: total > 0 ? ((data?.neutral / total) * 100).toFixed(1) : 0 
    },
    { 
      name: t('stats.neg'), 
      value: data?.negative || 0, 
      color: '#ef4444',
      percentage: total > 0 ? ((data?.negative / total) * 100).toFixed(1) : 0 
    },
  ];

  if (loading) {
    return (
      <div className="flex flex-col items-center justify-center h-96 text-purple-400">
        <motion.div animate={{ rotate: 360 }} transition={{ duration: 2, repeat: Infinity, ease: "linear"}} className='mb-4'>
          <Activity size={48} />
        </motion.div>
        <p className="font-mono text-sm uppercase tracking-[0.3em] animate-pulse">Neural Sync...</p>
      </div>
    );
  }

  return (
    <motion.div 
      initial={{ opacity: 0, x: 30 }} 
      animate={{ opacity: 1, x: 0 }} 
      className="p-4 md:p-8 max-w-5xl mx-auto"
    >
      <button 
        onClick={onBack} 
        className="flex items-center gap-2 text-gray-400 hover:text-purple-400 mb-6 md:mb-8 transition-all group font-bold tracking-widest text-[10px] md:text-xs uppercase"
      >
        <ArrowLeft size={16} className="group-hover:-translate-x-2 transition-transform" /> 
        {t('stats.back')}
      </button>

      {/* Grid de Cards: 1 columna en móvil, 3 en escritorio */}
      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4 md:gap-6 mb-8">
        <StatCard title={t('stats.total')} value={total} icon={<TrendingUp size={20} />} color="purple" />
        <StatCard title={t('stats.precision')} value="94.2%" icon={<Activity size={20} />} color="green" />
        <StatCard title={t('stats.uptime')} value="99.9%" icon={<Globe size={20} />} color="blue" />
      </div>

      <div className="bg-[#0f0817] p-5 md:p-8 rounded-3xl md:rounded-4xl border border-white/5 shadow-2xl relative overflow-hidden">
        <div className="flex flex-col lg:flex-row justify-between items-start lg:items-center mb-8 gap-6">
          <h3 className="text-xl md:text-2xl font-black flex items-center gap-3">
            <span className="w-1.5 md:w-2 h-6 md:h-8 bg-linear-to-b from-purple-500 to-blue-500 rounded-full" />
            {t('stats.global')}
          </h3>
          
          {/* Leyenda: Ajustada para que no se rompa en móvil */}
          <div className="flex flex-wrap gap-2 md:gap-4">
             {chartData.map((item) => (
              <div key={item.name} className="flex items-center gap-2 bg-white/5 px-3 py-1.5 rounded-full border border-white/10">
                <div className="w-2 h-2 rounded-full" style={{ backgroundColor: item.color }} />
                <span className="text-[9px] md:text-[10px] font-bold text-gray-400 uppercase">
                  {item.name}: {item.percentage}%
                </span>
              </div>  
             ))}
          </div>
        </div>     
        
        {/* Gráfica: Altura adaptable */}
        <div className="h-75 md:h-100 w-full">
          <ResponsiveContainer width="100%" height="100%">
            <BarChart data={chartData} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
              <XAxis 
                dataKey="name" 
                axisLine={false} 
                tickLine={false} 
                tick={{ fill: '#64748b', fontSize: 10, fontWeight: 600 }} 
                dy={10} 
              />
              <YAxis hide />
              <Tooltip 
                cursor={{ fill: 'white', opacity: 0.05 }}
                content={({ active, payload }) => {
                  if (active && payload && payload.length) {
                    return (
                      <div className="bg-[#1a1025] border border-white/10 p-3 md:p-4 rounded-xl md:rounded-2xl shadow-2xl">
                        <p className="text-[10px] text-gray-400 uppercase mb-1 font-bold">{payload[0].payload.name}</p>
                        <p className="text-xl md:text-2xl font-black text-white">{payload[0].value}</p>
                      </div>
                    );
                  }
                  return null;
                }}
              />
              <Bar 
                dataKey="value" 
                radius={[8, 8, 4, 4]} 
                barSize={window.innerWidth < 768 ? 40 : 65}
              >
                {chartData.map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={entry.color} fillOpacity={0.8} />
                ))}
              </Bar>
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>
    </motion.div>
  );
};

const StatCard = ({ title, value, icon, color }) => {
  const colors = {
    purple: "border-purple-500/20 text-purple-400",
    green: "border-emerald-500/20 text-emerald-400",
    blue: "border-blue-500/20 text-blue-400"
  };
  return (
    <div className={`p-5 md:p-6 rounded-2xl bg-white/5 border ${colors[color]} transition-all group hover:-translate-y-1`}>
      <div className="flex justify-between items-start mb-4">
        <div className="text-gray-400 text-[9px] md:text-[10px] uppercase tracking-widest font-bold">{title}</div>
        <div className="group-hover:scale-110 transition-transform opacity-70 group-hover:opacity-100">{icon}</div>
      </div>
      <div className="text-2xl md:text-3xl font-black text-white">{value}</div>
    </div>
  );
};

export default Stats;