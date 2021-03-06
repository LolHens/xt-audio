package com.xtaudio.xt;

/* Copyright (C) 2015-2017 Sjoerd van Kreel.
 *
 * This file is part of XT-Audio.
 *
 * XT-Audio is free software: you can redistribute it and/or modify it under the 
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * XT-Audio is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with XT-Audio. If not, see<http://www.gnu.org/licenses/>.
 */
public final class XtFormat {

    public XtMix mix = new XtMix();
    public int inputs;
    public long inMask;
    public int outputs;
    public long outMask;

    public XtFormat() {
    }
    
    public XtFormat(XtMix mix, int inputs, long inMask, int outputs, long outMask) {
        this.mix = mix;
        this.inputs = inputs;
        this.inMask = inMask;
        this.outputs = outputs;
        this.outMask = outMask;
    }

    @Override
    public String toString() {
        return XtPrint.formatToString(this);
    }
}
