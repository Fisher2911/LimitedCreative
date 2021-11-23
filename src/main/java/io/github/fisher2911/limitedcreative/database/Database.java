/*
 * Copyright 2021 Fisher2911
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.github.fisher2911.limitedcreative.database;

import io.github.fisher2911.limitedcreative.world.BlockPosition;

import java.util.UUID;

public interface Database {

    String TABLE_NAME = "block";
    String WORLD_UUID_COLUMN = "world_uuid";
    String CHUNK_KEY_COLUMN = "chunk_key";
    String POSITION_X_COLUMN = "position_x";
    String POSITION_Y_COLUMN = "position_y";
    String POSITION_Z_COLUMN = "position_z";

    void load();
    void shutdown();
    void saveAll();
    void saveChunkBlocks(final UUID worldUUID, final long chunkKey);
    void loadChunkBlocks(final UUID worldUUID, final long chunkKey);
    void deleteRemovedBlock(final BlockPosition blockPosition);
    void beginSaveAtInterval();
}
