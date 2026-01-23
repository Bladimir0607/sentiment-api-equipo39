import React, { useEffect, useState, mouseMoveEvent } from 'react';
import { motion, useScroll, useTransform } from 'framer-motion';

const StarBackground = () => {
  const [stars, setStars] = useState([]);
  const [mousePos, setMousePos] = useState({ x: 0, y: 0 });

  useEffect(() => {
    const generatedStars = Array.from({ length: 50 }).map((_, i) => ({
      id: i,
      size: Math.random() * 2 + 1,
      top: Math.random() * 100 + "%",
      left: Math.random() * 100 + "%",
      duration: Math.random() * 3 + 4,
      delay: Math.random() * 5,
      color: Math.random() > 0.8 ? '#a855f7' : '#ffffff', // Algunas estrellas son púrpuras
    }));
    setStars(generatedStars);

    const handleMouseMove = (e) => {
      setMousePos({ x: e.clientX, y: e.clientY });
    };
    window.addEventListener('mousemove', handleMouseMove);
    return () => window.removeEventListener('mousemove', handleMouseMove);
  }, []);

  return (
    <div className="fixed inset-0 z-0 pointer-events-none overflow-hidden bg-[#050208]">
      {/* Nebulosa de fondo que sigue al mouse */}
      <motion.div 
        className="absolute inset-0 opacity-30"
        animate={{
          background: `radial-gradient(circle at ${mousePos.x}px ${mousePos.y}px, rgba(147, 51, 234, 0.15) 0%, transparent 50%)`
        }}
      />

      {stars.map((star) => (
        <motion.div
          key={star.id}
          className="absolute rounded-full"
          style={{
            width: star.size + 'px',
            height: star.size + 'px',
            top: star.top,
            left: star.left,
            backgroundColor: star.color,
            boxShadow: `0 0 ${star.size * 2}px ${star.color}`,
          }}
          animate={{
            opacity: [0.2, 0.8, 0.2],
            scale: [1, 1.5, 1],
          }}
          transition={{
            duration: star.duration,
            repeat: Infinity,
            delay: star.delay,
            ease: "easeInOut",
          }}
        />
      ))}
    </div>
  );
};

export default StarBackground;