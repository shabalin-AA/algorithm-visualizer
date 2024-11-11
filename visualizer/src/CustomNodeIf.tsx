import {Node, Position, NodeProps} from "@xyflow/react"
import { useState } from 'react';
import "./styles.css"
import CustomHandle from "./CustomHandle"

type Text = Node<{label: string}>;

function CustomNode ({data}: NodeProps<Text>) {
  const [InputText, SetInputText] = useState("");
  data.label = InputText
  return (
    <div> 
      <div style={{
        width: "50px", 
        height: "50px",
        background: '#fff',
        border: '1px solid #777',
        transform: "rotate(45deg)",
        }}>
      </div>
      <input type="text" 
             className="node-label" 
             placeholder="a > b"
             value = {InputText}
             onChange = {e => SetInputText(e.target.value)}/>
      <div>
        <CustomHandle type="source" position={Position.Left}  style={{left: -9}}  connectioncount={1} id="false"/>
        <CustomHandle type="source" position={Position.Right} style={{right: -9}} connectioncount={1} id="true"/>
        <CustomHandle type="target" position={Position.Top}   style={{top: -9}}   connectioncount={-1}/>
      </div>   
    </div>
  );
};

export default CustomNode;
