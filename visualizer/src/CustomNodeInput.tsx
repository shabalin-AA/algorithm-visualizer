import { Node, Position, NodeProps} from "@xyflow/react"
import { useState } from 'react';
import "./styles.css"
import CustomHandle from "./CustomHandle"

type Text = Node<{label: string}>;

function CustomNodeInput ({data}: NodeProps<Text>) {
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
            <input type="text" value={InputText} className="node-label" placeholder="a = 10; b = 20" onChange={e => SetInputText(e.target.value)}/>
        </div> 
        <div>
            <CustomHandle type="source" position={Position.Bottom} connectioncount={1}/>
        </div>   
    </div>
  );
};

export default CustomNodeInput;
