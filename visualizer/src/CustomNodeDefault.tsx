import { Node, Position, NodeProps} from "@xyflow/react"
import { useState } from 'react';
import "./styles.css"
import CustomHandle from "./CustomHandle"

type Text = Node<{label: string}>;

function CustomNodeDefault ({data}: NodeProps<Text>) {
  const [InputText, SetInputText] = useState("");
  data.label = InputText
  return (
    <div>
        <div style={{
          width: "200px", 
          height: "30px",
          background: '#fff',
          border: '1px solid #777',
          }}>
            <input type="text" 
                   value={InputText} 
                   className="node-label" 
                   placeholder="c = b + a" 
                   onChange={e => SetInputText(e.target.value)}/>
        </div> 
        <div>
            <CustomHandle type="target" position={Position.Top} connectioncount={-1}/>
            <CustomHandle type="source" position={Position.Bottom} connectioncount={1}/>
        </div>   
    </div>
  );
};

export default CustomNodeDefault;
