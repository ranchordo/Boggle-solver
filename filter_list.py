import re
with open("words-orig.txt",'r') as f:
    with open("words.txt",'w+') as fo:
        while True:
            line=f.readline().strip().lower()
            if not line:
                break
            if len([1 for i in re.finditer('q(?!u)',line)])>0: #If it has a q but no u after it
                continue
            if len(line)>=3:
                fo.write(line+"\n")
