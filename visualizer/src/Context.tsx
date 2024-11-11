import React, { createContext, useContext, useState, ReactNode } from 'react';

type DnDContextType = [string | null, React.Dispatch<React.SetStateAction<string | null>>];

const DnDContext = createContext<DnDContextType>([null, () => {}]);

interface DnDProviderProps {
  children: ReactNode;
}

export const DnDProvider: React.FC<DnDProviderProps> = ({ children }) => {
  const [type, setType] = useState<string | null>(null);
  return (
    <DnDContext.Provider value={[type, setType]}>
      {children}
    </DnDContext.Provider>
  );
}

export default DnDContext;

export const useDnD = () => {
  return useContext(DnDContext);
}

