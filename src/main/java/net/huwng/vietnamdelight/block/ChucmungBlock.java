package net.huwng.vietnamdelight.block;

import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.particles.ParticleOptions;
import net.huwng.vietnamdelight.init.VietnamDelightModParticleTypes;

import java.util.ArrayList;
import java.util.List;

public class ChucmungBlock extends Block {
    public ChucmungBlock() {
        super(BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.GRAVEL).strength(1f, 10f));
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return 15;
    }
    
    // Khi block nhận tín hiệu redstone
    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
        
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            // Kiểm tra nếu có tín hiệu redstone
            if (level.hasNeighborSignal(pos)) {
                System.out.println("ChucmungBlock activated at: " + pos);
                
                // Tạo hiệu ứng 25 blocks phía trên
                BlockPos effectPos = pos.above(25);
                ParticleOptions particle = (ParticleOptions) VietnamDelightModParticleTypes.CHUCMUNGPARTICLE.get();
                
                // Tạo animation trong thread riêng
                createHeartAndTextAnimation(serverLevel, effectPos, particle);
            }
        }
    }
    
    private static void createHeartAndTextAnimation(ServerLevel level, BlockPos center, ParticleOptions particle) {
        new Thread(() -> {
            try {
                List<Position> heartPoints = getHeartShape(center, 10.0);
                List<Position> textPoints = getTextPoints(center, 3.5);
                
                System.out.println("Creating heart with " + heartPoints.size() + " points");
                System.out.println("Creating text with " + textPoints.size() + " points");
                
                // Phase 1: Vẽ trái tim từ từ
                List<Position> drawnPoints = new ArrayList<>();
                int heartPointsPerTick = Math.max(1, heartPoints.size() / 80);
                for (int i = 0; i < heartPoints.size(); i += heartPointsPerTick) {
                    final int startIndex = i;
                    level.getServer().execute(() -> {
                        for (int j = startIndex; j < Math.min(startIndex + heartPointsPerTick, heartPoints.size()); j++) {
                            Position p = heartPoints.get(j);
                            drawnPoints.add(p);
                            // Gửi tới các players trong range 512 blocks
                            sendParticleToAllNearby(level, particle, p.x, p.y, p.z, 512.0, 3, 0.01, 0.01, 0.01, 0.0);
                        }
                    });
                    Thread.sleep(40);
                }
                
                Thread.sleep(500);
                
                // Phase 2: Vẽ text "20/10"
                int textPointsPerTick = Math.max(1, textPoints.size() / 50);
                for (int i = 0; i < textPoints.size(); i += textPointsPerTick) {
                    final int startIndex = i;
                    level.getServer().execute(() -> {
                        for (int j = startIndex; j < Math.min(startIndex + textPointsPerTick, textPoints.size()); j++) {
                            Position p = textPoints.get(j);
                            drawnPoints.add(p);
                            sendParticleToAllNearby(level, particle, p.x, p.y, p.z, 512.0, 3, 0.01, 0.01, 0.01, 0.0);
                        }
                    });
                    Thread.sleep(40);
                }
                
                // Phase 3: Duy trì particles hiển thị - spawn lại liên tục
                System.out.println("Maintaining particles display for 10 seconds...");
                for (int tick = 0; tick < 200; tick++) { // 10 giây
                    final int currentTick = tick;
                    level.getServer().execute(() -> {
                        // Re-spawn tất cả particles đã vẽ với tần suất thưa hơn
                        for (int i = 0; i < drawnPoints.size(); i++) {
                            // Chỉ spawn lại 1 phần particles mỗi tick để giảm lag
                            if ((i + currentTick) % 3 == 0) {
                                Position p = drawnPoints.get(i);
                                sendParticleToAllNearby(level, particle, p.x, p.y, p.z, 512.0, 1, 0, 0, 0, 0.0);
                            }
                        }
                    });
                    Thread.sleep(50); // Spawn lại mỗi 50ms
                }
                
                // Phase 4: Particles bay lên (kết thúc)
                System.out.println("Starting finale effect...");
                for (int i = 0; i < 40; i++) {
                    level.getServer().execute(() -> {
                        for (int k = 0; k < 15; k++) {
                            Position p = drawnPoints.get((int)(Math.random() * drawnPoints.size()));
                            sendParticleToAllNearby(level, particle, p.x, p.y, p.z, 512.0, 1, 
                                (Math.random() - 0.5) * 0.3, 
                                Math.random() * 0.4 + 0.15, 
                                (Math.random() - 0.5) * 0.3, 
                                0.12);
                        }
                    });
                    Thread.sleep(80);
                }
                
                System.out.println("Animation completed!");
                
            } catch (Exception e) {
                System.out.println("Error in animation: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }
    
    private static void sendParticleToAllNearby(ServerLevel level, ParticleOptions particle, 
                                                double x, double y, double z, double range,
                                                int count, double xOffset, double yOffset, double zOffset, double speed) {
        level.players().forEach(player -> {
            double distSq = player.distanceToSqr(x, y, z);
            if (distSq < range * range) {
                level.sendParticles(player, particle, true, x, y, z, count, xOffset, yOffset, zOffset, speed);
            }
        });
    }
    
    private static List<Position> getHeartShape(BlockPos center, double radius) {
        List<Position> points = new ArrayList<>();
        double cx = center.getX() + 0.5;
        double cy = center.getY();
        double cz = center.getZ() + 0.5;
        
        double scale = radius / 10.0;
        
        for (double t = 0; t <= Math.PI * 2; t += 0.015) {
            double x = scale * 16 * Math.pow(Math.sin(t), 3);
            double y = scale * (13 * Math.cos(t) - 5 * Math.cos(2*t) - 2 * Math.cos(3*t) - Math.cos(4*t));
            points.add(new Position(cx + x, cy + y, cz));
        }
        
        for (double t = 0; t <= Math.PI * 2; t += 0.04) {
            for (double scaleInner = 0.80; scaleInner <= 0.95; scaleInner += 0.03) {
                double x = scale * 16 * Math.pow(Math.sin(t), 3) * scaleInner;
                double y = scale * (13 * Math.cos(t) - 5 * Math.cos(2*t) - 2 * Math.cos(3*t) - Math.cos(4*t)) * scaleInner;
                points.add(new Position(cx + x, cy + y, cz));
            }
        }
        
        return points;
    }
    
    private static List<Position> getTextPoints(BlockPos center, double size) {
        List<Position> points = new ArrayList<>();
        double cx = center.getX() + 0.5;
        double cy = center.getY();
        double cz = center.getZ() + 0.5;
        
        double spacing = size * 0.85;
        double step = 0.04;
        
        addDigit2(points, cx - spacing * 2.2, cy, cz, size, step);
        addDigit0(points, cx - spacing * 1.1, cy, cz, size, step);
        addSlash(points, cx, cy, cz, size, step);
        addDigit1(points, cx + spacing * 1.1, cy, cz, size, step);
        addDigit0(points, cx + spacing * 2.2, cy, cz, size, step);
        
        return points;
    }
    
    private static void addDigit2(List<Position> points, double cx, double cy, double cz, double size, double step) {
        for (double x = -size/2; x <= size/2; x += step) {
            points.add(new Position(cx + x, cy + size, cz));
            points.add(new Position(cx + x, cy + size - 0.08, cz));
        }
        for (double x = -size/2; x <= size/2; x += step) {
            points.add(new Position(cx + x, cy, cz));
            points.add(new Position(cx + x, cy - 0.08, cz));
        }
        for (double x = -size/2; x <= size/2; x += step) {
            points.add(new Position(cx + x, cy - size, cz));
            points.add(new Position(cx + x, cy - size + 0.08, cz));
        }
        for (double y = 0; y <= size; y += step) {
            points.add(new Position(cx + size/2, cy + y, cz));
            points.add(new Position(cx + size/2 - 0.08, cy + y, cz));
        }
        for (double y = -size; y <= 0; y += step) {
            points.add(new Position(cx - size/2, cy + y, cz));
            points.add(new Position(cx - size/2 + 0.08, cy + y, cz));
        }
    }
    
    private static void addDigit0(List<Position> points, double cx, double cy, double cz, double size, double step) {
        for (double t = 0; t < Math.PI * 2; t += step * 1.5) {
            double x = (size/2) * Math.cos(t);
            double y = size * Math.sin(t);
            points.add(new Position(cx + x, cy + y, cz));
        }
        for (double t = 0; t < Math.PI * 2; t += step * 2) {
            for (double innerScale = 0.85; innerScale <= 0.95; innerScale += 0.05) {
                double x = (size/2 - 0.1) * Math.cos(t) * innerScale;
                double y = (size - 0.15) * Math.sin(t) * innerScale;
                points.add(new Position(cx + x, cy + y, cz));
            }
        }
    }
    
    private static void addDigit1(List<Position> points, double cx, double cy, double cz, double size, double step) {
        for (double y = -size; y <= size; y += step) {
            points.add(new Position(cx, cy + y, cz));
            points.add(new Position(cx + 0.08, cy + y, cz));
            points.add(new Position(cx - 0.08, cy + y, cz));
        }
        for (double d = 0; d <= 0.4; d += step) {
            points.add(new Position(cx - d, cy + size - d, cz));
            points.add(new Position(cx - d + 0.08, cy + size - d, cz));
        }
        for (double x = -size/3; x <= size/3; x += step) {
            points.add(new Position(cx + x, cy - size, cz));
            points.add(new Position(cx + x, cy - size + 0.08, cz));
        }
    }
    
    private static void addSlash(List<Position> points, double cx, double cy, double cz, double size, double step) {
        for (double t = -size; t <= size; t += step) {
            points.add(new Position(cx + t * 0.25, cy + t, cz));
            points.add(new Position(cx + t * 0.25 + 0.08, cy + t, cz));
            points.add(new Position(cx + t * 0.25 - 0.08, cy + t, cz));
            points.add(new Position(cx + t * 0.25, cy + t + 0.08, cz));
        }
    }
    
    private static class Position {
        double x, y, z;
        Position(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}