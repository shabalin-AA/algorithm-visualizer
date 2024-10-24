import React, { FC } from 'react';
import { useDnD } from "./Context";

const DnDComponent: FC = () => {
  const [_, setType] = useDnD();

  const onDragStart = (event: React.DragEvent<HTMLDivElement>, nodeType: string): void => {
    setType(nodeType);
    event.dataTransfer.effectAllowed = 'move';
  };

  return (
    <aside>
      <div className="description">Перетащите узлы, чтобы создать их на поле.</div>
      <div className="dndnode input" onDragStart={(event) => onDragStart(event, 'input')} draggable>
        Input Node
      </div>
      <div className="dndnode" onDragStart={(event) => onDragStart(event, 'default')} draggable>
        Default Node
      </div>
      <div className="dndnode output" onDragStart={(event) => onDragStart(event, 'output')} draggable>
        Output Node
      </div>
      <div className="ifnode" onDragStart={(event) => onDragStart(event, 'CustomNode')} draggable>
        If Node
      </div>
    </aside>
  );
};

export default DnDComponent;