import net from 'node:net';

function encodeVarInt(value) {
  let remaining = value >>> 0;
  const bytes = [];
  do {
    let byte = remaining & 0x7f;
    remaining >>>= 7;
    if (remaining !== 0) byte |= 0x80;
    bytes.push(byte);
  } while (remaining !== 0);
  return Buffer.from(bytes);
}

function readVarInt(buffer, offset = 0) {
  let value = 0;
  let count = 0;
  let byte;
  do {
    if (offset + count >= buffer.length) return null;
    byte = buffer[offset + count];
    value |= (byte & 0x7f) << (7 * count);
    count += 1;
    if (count > 5) throw new Error('Invalid Minecraft status packet VarInt.');
  } while ((byte & 0x80) !== 0);
  return { value: value >>> 0, bytes: count };
}

function framePacket(payload) {
  return Buffer.concat([encodeVarInt(payload.length), payload]);
}

function encodeString(value) {
  const bytes = Buffer.from(value, 'utf8');
  return Buffer.concat([encodeVarInt(bytes.length), bytes]);
}

/** Query Java Edition status and measure the server's ping response time. */
export function queryMinecraftServer(host, port = 25565, timeoutMs = 5000) {
  return new Promise((resolve, reject) => {
    const socket = net.createConnection({ host, port });
    let buffer = Buffer.alloc(0);
    let statusReceived = false;
    let pingStarted = 0;
    let settled = false;

    const timer = setTimeout(() => finish(new Error('Minecraft server status request timed out.')), timeoutMs);

    function finish(error, result) {
      if (settled) return;
      settled = true;
      clearTimeout(timer);
      socket.destroy();
      if (error) reject(error);
      else resolve(result);
    }

    socket.once('error', error => finish(error));
    socket.once('connect', () => {
      socket.setNoDelay(true);
      const address = encodeString(host);
      const handshake = Buffer.concat([
        encodeVarInt(0),
        encodeVarInt(767),
        address,
        Buffer.from([(port >> 8) & 0xff, port & 0xff]),
        encodeVarInt(1)
      ]);
      socket.write(framePacket(handshake));
      socket.write(framePacket(Buffer.from([0])));
    });

    socket.on('data', chunk => {
      buffer = Buffer.concat([buffer, chunk]);
      while (buffer.length > 0) {
        let frameLength;
        try {
          frameLength = readVarInt(buffer);
        } catch (error) {
          finish(error);
          return;
        }
        if (!frameLength) return;
        const frameEnd = frameLength.bytes + frameLength.value;
        if (buffer.length < frameEnd) return;
        const packet = buffer.subarray(frameLength.bytes, frameEnd);
        buffer = buffer.subarray(frameEnd);

        const packetId = readVarInt(packet);
        if (!packetId) continue;
        if (packetId.value === 0 && !statusReceived) {
          const stringLength = readVarInt(packet, packetId.bytes);
          if (!stringLength) {
            finish(new Error('Minecraft server returned an incomplete status response.'));
            return;
          }
          const start = packetId.bytes + stringLength.bytes;
          try {
            const status = JSON.parse(packet.subarray(start, start + stringLength.value).toString('utf8'));
            statusReceived = true;
            const payload = Buffer.alloc(8);
            payload.writeBigInt64BE(BigInt(Date.now()));
            pingStarted = Date.now();
            socket.write(framePacket(Buffer.concat([encodeVarInt(1), payload])));
            socket.once('close', () => {
              if (!settled && statusReceived) finish(new Error('Minecraft server closed before replying to ping.'));
            });
            socket._economyStatus = status;
          } catch (error) {
            finish(error);
            return;
          }
        } else if (packetId.value === 1 && statusReceived) {
          finish(null, { status: socket._economyStatus, ping: Math.max(0, Date.now() - pingStarted) });
          return;
        }
      }
    });
  });
}
