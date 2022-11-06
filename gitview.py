import sys
import zlib
filename=sys.argv[1]
compressed_contents=open(filename,'rb').read()
decompressed_contents=zlib.decompress(compressed_contents)
try:
    print(decompressed_contents.decode('utf-8'))
except:
    print(compressed_contents)