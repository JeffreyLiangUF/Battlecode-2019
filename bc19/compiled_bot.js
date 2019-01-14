var __extends = (this && this.__extends) || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
    function __() { this.constructor = d; }
    d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
};
/* Generated from Java with JSweet 2.0.0-SNAPSHOT - http://www.jsweet.org */
var bc19;
(function (bc19) {
    var Action = (function () {
        function Action(signal, signalRadius, logs, castleTalk) {
            this.signal = 0;
            this.signal_radius = 0;
            this.logs = null;
            this.castle_talk = 0;
            this.signal = signal;
            this.signal_radius = signalRadius;
            this.logs = logs;
            this.castle_talk = castleTalk;
        }
        return Action;
    }());
    bc19.Action = Action;
    Action["__class"] = "bc19.Action";
})(bc19 || (bc19 = {}));
(function (bc19) {
    var MovingRobot = (function () {
        function MovingRobot() {
        }
        MovingRobot.UpdateFlood = function (robo, map, floodMap, stepDistance, hopDistance, refine) {
            var boundaries = ([]);
            for (var y = 0; y < map.length; y++) {
                for (var x = 0; x < map[0].length; x++) {
                    if (!map[y][x]) {
                        MovingRobot.AddBoundary(boundaries, map, floodMap, new bc19.Position(y, x));
                    }
                }
                ;
            }
            ;
            var hopPositions = ([]);
            for (var i = 0; i < boundaries.length; i++) {
                for (var y = -stepDistance; y < stepDistance; y++) {
                    for (var x = -stepDistance; x <= -stepDistance + 1; x++) {
                        var pos1 = new bc19.Position(/* get */ boundaries[i].y + y, /* get */ boundaries[i].x + x);
                        if (bc19.Helper.inMap(map, pos1) && floodMap[pos1.y][pos1.x] > 0) {
                            var pos2 = boundaries[i];
                            var pos1Value = floodMap[pos1.y][pos1.x];
                            var pos2Value = floodMap[pos2.y][pos2.x];
                            if (Math.abs(Math.fround(pos1Value - pos2Value)) > hopDistance && MovingRobot.Boundary(map, pos1)) {
                                if (bc19.Helper.DistanceSquared(pos1, pos2) <= stepDistance * stepDistance) {
                                    if (pos1Value > pos2Value) {
                                        floodMap[pos1.y][pos1.x] = Math.fround(pos2Value + stepDistance);
                                        /* add */ (hopPositions.push(pos1) > 0);
                                    }
                                    else {
                                        floodMap[pos2.y][pos2.x] = Math.fround(pos1Value + stepDistance);
                                        /* add */ (hopPositions.push(pos2) > 0);
                                    }
                                }
                            }
                        }
                    }
                    ;
                    for (var x = stepDistance - 1; x <= stepDistance; x++) {
                        var pos1 = new bc19.Position(/* get */ boundaries[i].y + y, /* get */ boundaries[i].x + x);
                        if (bc19.Helper.inMap(map, pos1) && floodMap[pos1.y][pos1.x] > 0) {
                            var pos2 = boundaries[i];
                            var pos1Value = floodMap[pos1.y][pos1.x];
                            var pos2Value = floodMap[pos2.y][pos2.x];
                            if (Math.abs(Math.fround(pos1Value - pos2Value)) > hopDistance && MovingRobot.Boundary(map, pos1)) {
                                if (bc19.Helper.DistanceSquared(pos1, pos2) <= stepDistance * stepDistance) {
                                    if (pos1Value > pos2Value) {
                                        floodMap[pos1.y][pos1.x] = Math.fround(pos2Value + stepDistance);
                                        /* add */ (hopPositions.push(pos1) > 0);
                                    }
                                    else {
                                        floodMap[pos2.y][pos2.x] = Math.fround(pos1Value + stepDistance);
                                        /* add */ (hopPositions.push(pos2) > 0);
                                    }
                                }
                            }
                        }
                    }
                    ;
                }
                ;
            }
            ;
            if (refine) {
                for (var i = 0; i < hopPositions.length; i++) {
                    floodMap = MovingRobot.ReiteratePath(robo, map, floodMap, /* get */ hopPositions[i], floodMap[hopPositions[i].y][hopPositions[i].x]);
                }
                ;
            }
            return floodMap;
        };
        MovingRobot.AddBoundary = function (boundaries, map, floodMap, pos) {
            var down = new bc19.Position(pos.y + 1, pos.x);
            if (bc19.Helper.inMap(map, down) && floodMap[down.y][down.x] > 0) {
                /* add */ (boundaries.push(down) > 0);
            }
            var up = new bc19.Position(pos.y - 1, pos.x);
            if (bc19.Helper.inMap(map, up) && floodMap[up.y][up.x] > 0) {
                /* add */ (boundaries.push(up) > 0);
            }
            var right = new bc19.Position(pos.y, pos.x + 1);
            if (bc19.Helper.inMap(map, right) && floodMap[right.y][right.x] > 0) {
                /* add */ (boundaries.push(right) > 0);
            }
            var left = new bc19.Position(pos.y, pos.x - 1);
            if (bc19.Helper.inMap(map, left) && floodMap[left.y][left.x] > 0) {
                /* add */ (boundaries.push(left) > 0);
            }
        };
        MovingRobot.Boundary = function (map, pos) {
            if (bc19.Helper.inMap(map, new bc19.Position(pos.y - 1, pos.x)) && !map[pos.y - 1][pos.x]) {
                return true;
            }
            if (bc19.Helper.inMap(map, new bc19.Position(pos.y + 1, pos.x)) && !map[pos.y + 1][pos.x]) {
                return true;
            }
            if (bc19.Helper.inMap(map, new bc19.Position(pos.y, pos.x - 1)) && !map[pos.y][pos.x - 1]) {
                return true;
            }
            if (bc19.Helper.inMap(map, new bc19.Position(pos.y, pos.x + 1)) && !map[pos.y][pos.x + 1]) {
                return true;
            }
            return false;
        };
        MovingRobot.ReiteratePath = function (robo, map, floodPath, pos, stopValue) {
            var singleStep = floodPath;
            var toBeVisited = ([]);
            /* add */ (toBeVisited.push(new bc19.PathingPosition(pos, Math.fround(stopValue - 1))) > 0);
            while ((toBeVisited.length > 0)) {
                var removed = (function (a) { return a.length == 0 ? null : a.shift(); })(toBeVisited);
                var cum = removed.cumulative;
                for (var y = -1; y <= 1; y++) {
                    for (var x = -1; x <= 1; x++) {
                        if ((x * x + y * y) === 1 && bc19.Helper.inMap(map, new bc19.Position(removed.pos.y + y, removed.pos.x + x))) {
                            if (singleStep[removed.pos.y + y][removed.pos.x + x] > 0 && (Math.fround(removed.cumulative - singleStep[removed.pos.y + y][removed.pos.x + x])) > 1) {
                                cum = Math.fround(singleStep[removed.pos.y + y][removed.pos.x + x] + 1);
                            }
                        }
                    }
                    ;
                }
                ;
                singleStep[removed.pos.y][removed.pos.x] = map[removed.pos.y][removed.pos.x] ? cum : -1;
                if (map[removed.pos.y][removed.pos.x]) {
                    for (var y = -1; y <= 1; y++) {
                        for (var x = -1; x <= 1; x++) {
                            var newCumulitive = cum;
                            if (x === 0 && y === 0) {
                                continue;
                            }
                            if (x * x === 1 && y * y === 1) {
                                newCumulitive += 1.4;
                            }
                            else {
                                newCumulitive += 1;
                            }
                            var relativePosition = new bc19.Position(removed.pos.y + y, removed.pos.x + x);
                            var relative = new bc19.PathingPosition(relativePosition, newCumulitive);
                            if (bc19.Helper.inMap(map, relative.pos) && singleStep[relative.pos.y][relative.pos.x] > singleStep[removed.pos.y][removed.pos.x]) {
                                /* add */ (toBeVisited.push(relative) > 0);
                                singleStep[relative.pos.y][relative.pos.x] = -2;
                            }
                        }
                        ;
                    }
                    ;
                }
            }
            ;
            return singleStep;
        };
        MovingRobot.CreateLayeredFloodPath = function (map, pos) {
            var singleStep = (function (dims) { var allocate = function (dims) { if (dims.length == 0) {
                return 0;
            }
            else {
                var array = [];
                for (var i = 0; i < dims[0]; i++) {
                    array.push(allocate(dims.slice(1)));
                }
                return array;
            } }; return allocate(dims); })([map.length, map[0].length]);
            var toBeVisited = ([]);
            /* add */ (toBeVisited.push(new bc19.PathingPosition(pos, 0)) > 0);
            while ((toBeVisited.length > 0)) {
                var removed = (function (a) { return a.length == 0 ? null : a.shift(); })(toBeVisited);
                var cum = removed.cumulative;
                for (var y = -1; y <= 1; y++) {
                    for (var x = -1; x <= 1; x++) {
                        if ((x * x + y * y) === 1 && bc19.Helper.inMap(map, new bc19.Position(removed.pos.y + y, removed.pos.x + x))) {
                            if (singleStep[removed.pos.y + y][removed.pos.x + x] > 0 && (Math.fround(removed.cumulative - singleStep[removed.pos.y + y][removed.pos.x + x])) > 1) {
                                cum = Math.fround(singleStep[removed.pos.y + y][removed.pos.x + x] + 1);
                            }
                        }
                    }
                    ;
                }
                ;
                singleStep[removed.pos.y][removed.pos.x] = map[removed.pos.y][removed.pos.x] ? cum : -1;
                if (map[removed.pos.y][removed.pos.x]) {
                    for (var y = -1; y <= 1; y++) {
                        for (var x = -1; x <= 1; x++) {
                            var newCumulitive = cum;
                            if (x === 0 && y === 0) {
                                continue;
                            }
                            if (x * x === 1 && y * y === 1) {
                                newCumulitive += 1.4;
                            }
                            else {
                                newCumulitive += 1;
                            }
                            var relativePosition = new bc19.Position(removed.pos.y + y, removed.pos.x + x);
                            var relative = new bc19.PathingPosition(relativePosition, newCumulitive);
                            if (bc19.Helper.inMap(map, relative.pos) && singleStep[relative.pos.y][relative.pos.x] === 0) {
                                /* add */ (toBeVisited.push(relative) > 0);
                                singleStep[relative.pos.y][relative.pos.x] = -2;
                            }
                        }
                        ;
                    }
                    ;
                }
            }
            ;
            singleStep[pos.y][pos.x] = 0;
            return singleStep;
        };
        MovingRobot.prototype.PathingDistance = function (robot, path) {
            return path[robot.me.y][robot.me.x];
        };
        MovingRobot.prototype.FloodPathing = function (robot, path) {
            if (path == null) {
                return null;
            }
            var validPositions = bc19.Helper.AllPassableInRange(robot.map, new bc19.Position(robot.me.y, robot.me.x), robot.SPECS.UNITS[robot.me.unit].SPEED);
            var lowest = Number.MAX_VALUE;
            var lowestPos = null;
            for (var i = 0; i < validPositions.length; i++) {
                if (path[validPositions[i].y][validPositions[i].x] < lowest && path[validPositions[i].y][validPositions[i].x] > 0) {
                    lowest = path[validPositions[i].y][validPositions[i].x];
                    lowestPos = validPositions[i];
                }
            }
            ;
            return robot.move(lowestPos.x - robot.me.x, lowestPos.y - robot.me.y);
        };
        MovingRobot.prototype.ReadInitialSignals = function (robot, castleLocations) {
            var spawnCastle = robot.me;
            {
                var array122 = robot.getVisibleRobots();
                for (var index121 = 0; index121 < array122.length; index121++) {
                    var r = array122[index121];
                    {
                        if (r.unit === robot.SPECS.CASTLE && bc19.Helper.DistanceSquared(new bc19.Position(robot.me.y, robot.me.x), new bc19.Position(r.y, r.x)) < 2) {
                            spawnCastle = r;
                        }
                    }
                }
            }
            var spawnCastlePos = new bc19.Position(spawnCastle.y, spawnCastle.x);
            if (castleLocations.length === 0) {
                /* add */ (castleLocations.push(spawnCastlePos) > 0);
            }
            var signal = spawnCastle.signal;
            if (signal === -1) {
                return true;
            }
            var x = signal & 63;
            signal -= x;
            signal >>= 6;
            var y = signal & 63;
            signal -= y;
            signal >>= 6;
            var numCastle = signal & 3;
            if (numCastle === 2) {
                /* add */ (castleLocations.push(new bc19.Position(y, x)) > 0);
                return true;
            }
            else {
                /* add */ (castleLocations.push(new bc19.Position(y, x)) > 0);
                return false;
            }
        };
        return MovingRobot;
    }());
    bc19.MovingRobot = MovingRobot;
    MovingRobot["__class"] = "bc19.MovingRobot";
    var PathingPosition = (function () {
        function PathingPosition(p, c) {
            this.pos = null;
            this.cumulative = 0;
            this.pos = p;
            this.cumulative = c;
        }
        return PathingPosition;
    }());
    bc19.PathingPosition = PathingPosition;
    PathingPosition["__class"] = "bc19.PathingPosition";
})(bc19 || (bc19 = {}));
(function (bc19) {
    var Castle = (function () {
        function Castle(robot) {
            this.initialized = false;
            this.robot = null;
            this.mapIsHorizontal = false;
            this.ourTeam = 0;
            this.numCastles = 0;
            this.location = null;
            this.ourCastles = null;
            this.enemyCastles = null;
            this.idDone = 0;
            this.robot = robot;
        }
        Castle.prototype.Execute = function () {
            if (!this.initialized) {
                this.Initialize();
            }
            if (this.robot.me.turn < 6) {
                this.DeclareAllyCastlePositions(false, false, 2);
            }
            return null;
        };
        Castle.prototype.Initialize = function () {
            if (this.robot.me.turn === 1) {
                this.InitializeVariables();
            }
            if (!this.initialized) {
                this.initialized = this.SetupAllyCastles();
                this.FindEnemyCastles();
            }
        };
        Castle.prototype.InitializeVariables = function () {
            this.mapIsHorizontal = bc19.Helper.FindSymmetry(this.robot.map);
            this.ourTeam = this.robot.me.team === this.robot.SPECS.RED ? 0 : 1;
            this.ourCastles = ({});
            this.enemyCastles = ({});
            this.idDone = 0;
            this.location = new bc19.Position(this.robot.me.y, this.robot.me.x);
        };
        Castle.prototype.SetupAllyCastles = function () {
            var robots = this.robot.getVisibleRobots();
            if (robots.length === 1) {
                this.numCastles = 1;
                /* put */ (function (m, k, v) { if (m.entries == null)
                    m.entries = []; for (var i = 0; i < m.entries.length; i++)
                    if (m.entries[i].key.equals != null && m.entries[i].key.equals(k) || m.entries[i].key === k) {
                        m.entries[i].value = v;
                        return;
                    } m.entries.push({ key: k, value: v, getKey: function () { return this.key; }, getValue: function () { return this.value; } }); })(this.ourCastles, this.robot.me.id, this.location);
                return true;
            }
            var castlesTalking = 0;
            for (var i = 0; i < robots.length; i++) {
                if (robots[i].castle_talk > 0) {
                    castlesTalking++;
                }
            }
            ;
            if (robots.length > 1 && castlesTalking === 0) {
                this.numCastles = robots.length;
            }
            else {
                for (var i = 0; i < robots.length; i++) {
                    if (robots[i].team === this.ourTeam && robots[i].castle_talk > 0) {
                        var info = new bc19.CastleLocation(robots[i].castle_talk);
                        this.numCastles = info.threeCastles ? 3 : 2;
                        if ((function (m, k) { if (m.entries == null)
                            m.entries = []; for (var i_1 = 0; i_1 < m.entries.length; i_1++)
                            if (m.entries[i_1].key.equals != null && m.entries[i_1].key.equals(k) || m.entries[i_1].key === k) {
                                return true;
                            } return false; })(this.ourCastles, robots[i].id)) {
                            var current = (function (m, k) { if (m.entries == null)
                                m.entries = []; for (var i_2 = 0; i_2 < m.entries.length; i_2++)
                                if (m.entries[i_2].key.equals != null && m.entries[i_2].key.equals(k) || m.entries[i_2].key === k) {
                                    return m.entries[i_2].value;
                                } return null; })(this.ourCastles, robots[i].id);
                            var input = info.yValue ? new bc19.Position(info.location, current.x) : new bc19.Position(current.y, info.location);
                            /* put */ (function (m, k, v) { if (m.entries == null)
                                m.entries = []; for (var i_3 = 0; i_3 < m.entries.length; i_3++)
                                if (m.entries[i_3].key.equals != null && m.entries[i_3].key.equals(k) || m.entries[i_3].key === k) {
                                    m.entries[i_3].value = v;
                                    return;
                                } m.entries.push({ key: k, value: v, getKey: function () { return this.key; }, getValue: function () { return this.value; } }); })(this.ourCastles, robots[i].id, input);
                        }
                        else {
                            var input = info.yValue ? new bc19.Position(info.location, -1) : new bc19.Position(-1, info.location);
                            /* put */ (function (m, k, v) { if (m.entries == null)
                                m.entries = []; for (var i_4 = 0; i_4 < m.entries.length; i_4++)
                                if (m.entries[i_4].key.equals != null && m.entries[i_4].key.equals(k) || m.entries[i_4].key === k) {
                                    m.entries[i_4].value = v;
                                    return;
                                } m.entries.push({ key: k, value: v, getKey: function () { return this.key; }, getValue: function () { return this.value; } }); })(this.ourCastles, robots[i].id, input);
                        }
                    }
                }
                ;
            }
            if ((function (m, k) { if (m.entries == null)
                m.entries = []; for (var i = 0; i < m.entries.length; i++)
                if (m.entries[i].key.equals != null && m.entries[i].key.equals(k) || m.entries[i].key === k) {
                    return true;
                } return false; })(this.ourCastles, this.robot.me.id)) {
                this.robot.castleTalk(this.CastleInfoTalk(this.numCastles === 3 ? true : false, false, this.location.x));
            }
            else {
                /* put */ (function (m, k, v) { if (m.entries == null)
                    m.entries = []; for (var i = 0; i < m.entries.length; i++)
                    if (m.entries[i].key.equals != null && m.entries[i].key.equals(k) || m.entries[i].key === k) {
                        m.entries[i].value = v;
                        return;
                    } m.entries.push({ key: k, value: v, getKey: function () { return this.key; }, getValue: function () { return this.value; } }); })(this.ourCastles, this.robot.me.id, this.location);
                this.robot.castleTalk(this.CastleInfoTalk(this.numCastles === 3 ? true : false, true, this.location.y));
            }
            return this.CheckComplete();
        };
        Castle.prototype.CastleInfoTalk = function (three, yValue, value) {
            var output = value;
            output += three ? 128 : 0;
            output += yValue ? 64 : 0;
            return output;
        };
        Castle.prototype.CheckComplete = function () {
            if ((function (m) { if (m.entries == null)
                m.entries = []; return m.entries.length; })(this.ourCastles) < this.numCastles) {
                return false;
            }
            {
                var array124 = (function (m) { var r = []; if (m.entries == null)
                    m.entries = []; for (var i = 0; i < m.entries.length; i++)
                    r.push(m.entries[i].value); return r; })(this.ourCastles);
                for (var index123 = 0; index123 < array124.length; index123++) {
                    var pos = array124[index123];
                    {
                        if (pos.y === -1 || pos.x === -1) {
                            return false;
                        }
                    }
                }
            }
            return true;
        };
        Castle.prototype.FindEnemyCastles = function () {
            {
                var array126 = (function (m) { if (m.entries == null)
                    m.entries = []; return m.entries; })(this.ourCastles);
                for (var index125 = 0; index125 < array126.length; index125++) {
                    var entry = array126[index125];
                    {
                        /* put */ (function (m, k, v) { if (m.entries == null)
                            m.entries = []; for (var i = 0; i < m.entries.length; i++)
                            if (m.entries[i].key.equals != null && m.entries[i].key.equals(k) || m.entries[i].key === k) {
                                m.entries[i].value = v;
                                return;
                            } m.entries.push({ key: k, value: v, getKey: function () { return this.key; }, getValue: function () { return this.value; } }); })(this.enemyCastles, entry.getKey(), bc19.Helper.FindEnemyCastle(this.robot.map, this.mapIsHorizontal, entry.getValue()));
                    }
                }
            }
        };
        Castle.prototype.DeclareAllyCastlePositions = function (bit1, bit2, radius) {
            {
                var array128 = (function (m) { if (m.entries == null)
                    m.entries = []; return m.entries; })(this.ourCastles);
                for (var index127 = 0; index127 < array128.length; index127++) {
                    var entry = array128[index127];
                    {
                    }
                }
            }
            if (this.numCastles === 1) {
                return;
            }
            else if (this.numCastles === 2) {
                var other = new bc19.Position(-1, -1);
                {
                    var array130 = (function (m) { var r = []; if (m.entries == null)
                        m.entries = []; for (var i = 0; i < m.entries.length; i++)
                        r.push(m.entries[i].key); return r; })(this.ourCastles);
                    for (var index129 = 0; index129 < array130.length; index129++) {
                        var id = array130[index129];
                        {
                            if (id !== this.robot.id) {
                                other = (function (m, k) { if (m.entries == null)
                                    m.entries = []; for (var i = 0; i < m.entries.length; i++)
                                    if (m.entries[i].key.equals != null && m.entries[i].key.equals(k) || m.entries[i].key === k) {
                                        return m.entries[i].value;
                                    } return null; })(this.ourCastles, id);
                            }
                        }
                    }
                }
                if (other.x >= 0 && other.y >= 0) {
                    this.robot.signal(this.BinarySignalsForInitialization(bit1, bit2, other), radius);
                }
            }
            else {
                if (this.idDone === 0) {
                    var other = new bc19.Position(-1, -1);
                    {
                        var array132 = (function (m) { var r = []; if (m.entries == null)
                            m.entries = []; for (var i = 0; i < m.entries.length; i++)
                            r.push(m.entries[i].key); return r; })(this.ourCastles);
                        for (var index131 = 0; index131 < array132.length; index131++) {
                            var id = array132[index131];
                            {
                                if (id !== this.robot.id) {
                                    this.idDone = id;
                                    other = (function (m, k) { if (m.entries == null)
                                        m.entries = []; for (var i = 0; i < m.entries.length; i++)
                                        if (m.entries[i].key.equals != null && m.entries[i].key.equals(k) || m.entries[i].key === k) {
                                            return m.entries[i].value;
                                        } return null; })(this.ourCastles, id);
                                }
                            }
                        }
                    }
                    if (other.x >= 0 && other.y >= 0) {
                        this.robot.signal(this.BinarySignalsForInitialization(bit1, bit2, other), radius);
                    }
                }
                else {
                    var other = new bc19.Position(-1, -1);
                    {
                        var array134 = (function (m) { var r = []; if (m.entries == null)
                            m.entries = []; for (var i = 0; i < m.entries.length; i++)
                            r.push(m.entries[i].key); return r; })(this.ourCastles);
                        for (var index133 = 0; index133 < array134.length; index133++) {
                            var id = array134[index133];
                            {
                                if (id !== this.robot.id && id !== this.idDone) {
                                    this.idDone = 0;
                                    other = (function (m, k) { if (m.entries == null)
                                        m.entries = []; for (var i = 0; i < m.entries.length; i++)
                                        if (m.entries[i].key.equals != null && m.entries[i].key.equals(k) || m.entries[i].key === k) {
                                            return m.entries[i].value;
                                        } return null; })(this.ourCastles, id);
                                }
                            }
                        }
                    }
                    if (other.x >= 0 && other.y >= 0) {
                        this.robot.signal(this.BinarySignalsForInitialization(bit1, bit2, other), radius);
                    }
                }
            }
        };
        Castle.prototype.BinarySignalsForInitialization = function (bit1, bit2, pos) {
            var output = ((bit1 ? 1 : 0) | 0);
            output <<= 1;
            output += bit2 ? 1 : 0;
            output <<= 1;
            output += this.numCastles;
            output <<= 2;
            output <<= 6;
            output += pos.y;
            output <<= 6;
            output += pos.x;
            return output;
        };
        Castle.prototype.ClosestCastleToKarb = function () {
            var karbMap = this.robot.getKarboniteMap();
            var lowestCastleDistance = Number.MAX_VALUE;
            var closestCastle = null;
            {
                var array136 = (function (m) { var r = []; if (m.entries == null)
                    m.entries = []; for (var i = 0; i < m.entries.length; i++)
                    r.push(m.entries[i].value); return r; })(this.ourCastles);
                for (var index135 = 0; index135 < array136.length; index135++) {
                    var castlePos = array136[index135];
                    {
                        var lowestDist = Number.MAX_VALUE;
                        for (var i = 0; i < karbMap.length; i++) {
                            for (var j = 0; j < karbMap[0].length; j++) {
                                if (karbMap[i][j] === true) {
                                    var karb = new bc19.Position(i, j);
                                    var least = bc19.Helper.DistanceSquared(castlePos, karb);
                                    if (least < lowestDist) {
                                        lowestDist = least;
                                    }
                                }
                                else {
                                    continue;
                                }
                            }
                            ;
                        }
                        ;
                        if (lowestDist < lowestCastleDistance) {
                            lowestCastleDistance = lowestDist;
                            closestCastle = castlePos;
                        }
                    }
                }
            }
            return closestCastle;
        };
        return Castle;
    }());
    bc19.Castle = Castle;
    Castle["__class"] = "bc19.Castle";
    Castle["__interfaces"] = ["bc19.Machine"];
    var CastleLocation = (function () {
        function CastleLocation(value) {
            this.threeCastles = false;
            this.yValue = false;
            this.location = 0;
            if (value > 127) {
                this.threeCastles = true;
                value -= 128;
            }
            else {
                this.threeCastles = false;
            }
            if (value > 63) {
                this.yValue = true;
                value -= 64;
            }
            else {
                this.yValue = false;
            }
            this.location = value;
        }
        return CastleLocation;
    }());
    bc19.CastleLocation = CastleLocation;
    CastleLocation["__class"] = "bc19.CastleLocation";
})(bc19 || (bc19 = {}));
(function (bc19) {
    var Helper = (function () {
        function Helper() {
        }
        Helper.inMap = function (map, pos) {
            if (pos.y < 0 || pos.y > (map.length - 1) || pos.x < 0 || pos.x > (map[0].length - 1)) {
                return false;
            }
            return true;
        };
        Helper.AllPassableInRange = function (map, pos, r) {
            var validPositions = ([]);
            for (var i = -r; i <= r; i++) {
                for (var j = -r; j <= r; j++) {
                    var y = (pos.y + i);
                    var x = (pos.x + j);
                    if (!Helper.inMap(map, new bc19.Position(y, x))) {
                        continue;
                    }
                    if (!map[y][x]) {
                        continue;
                    }
                    var distanceSquared = (y - pos.y) * (y - pos.y) + (x - pos.x) * (x - pos.x);
                    if (distanceSquared > r) {
                        continue;
                    }
                    /* add */ (validPositions.push(new bc19.Position(y, x)) > 0);
                }
                ;
            }
            ;
            return validPositions.slice(0);
        };
        Helper.DistanceSquared = function (pos1, pos2) {
            return (pos2.y - pos1.y) * (pos2.y - pos1.y) + (pos2.x - pos1.x) * (pos2.x - pos1.x);
        };
        Helper.FindSymmetry = function (map) {
            var mapIsHorizontal = true;
            for (var i = 0; i < (map.length / 2 | 0); i++) {
                for (var j = 0; j < map[i].length; j++) {
                    if (map[i][j] !== map[(map.length - 1) - i][j]) {
                        mapIsHorizontal = false;
                        return mapIsHorizontal;
                    }
                }
                ;
            }
            ;
            return mapIsHorizontal;
        };
        Helper.FindEnemyCastle = function (map, mapIsHorizontal, ourCastle) {
            return mapIsHorizontal ? new bc19.Position(ourCastle.y, (map[0].length - 1) - ourCastle.x) : new bc19.Position((map.length - 1) - ourCastle.y, ourCastle.x);
        };
        Helper.RobotAtPosition = function (robot, pos) {
            var visibleRobots = robot.getVisibleRobots();
            for (var i = 0; i < visibleRobots.length; i++) {
                if (pos.y === visibleRobots[i].y && pos.x === visibleRobots[i].x) {
                    return visibleRobots[i];
                }
            }
            ;
            return null;
        };
        Helper.PositionInVision = function (robot, pos) {
            var robotPos = new bc19.Position(robot.me.y, robot.me.x);
            if (Helper.DistanceSquared(robotPos, pos) <= robot.SPECS.UNITS[robot.me.unit].VISION_RADIUS) {
                return true;
            }
            return false;
        };
        Helper.IsSurroundingsOccupied = function (robot, map, pos) {
            for (var i = -1; i < 1; i++) {
                for (var j = -1; j < 1; j++) {
                    if (map[pos.y + i][pos.x + j] === 0) {
                        return false;
                    }
                }
                ;
            }
            ;
            return true;
        };
        Helper.RandomNonResourceAdjacentPosition = function (robot, pos) {
            var robots = robot.getVisibleRobotMap();
            var fuelMap = robot.getFuelMap();
            var karbMap = robot.getKarboniteMap();
            for (var i = -1; i < 1; i++) {
                for (var j = -1; j < 1; j++) {
                    if (robots[pos.y + i][pos.x + j] === 0 && fuelMap[pos.y + i][pos.x + j] === false && karbMap[pos.y + i][pos.x + j] === false) {
                        return new bc19.Position(pos.y + i, pos.x + j);
                    }
                }
                ;
            }
            ;
            return null;
        };
        return Helper;
    }());
    bc19.Helper = Helper;
    Helper["__class"] = "bc19.Helper";
})(bc19 || (bc19 = {}));
(function (bc19) {
    var BCException = (function (_super) {
        __extends(BCException, _super);
        function BCException(errorMessage) {
            var _this = _super.call(this, errorMessage) || this;
            _this.message = errorMessage;
            Object.setPrototypeOf(_this, BCException.prototype);
            return _this;
        }
        return BCException;
    }(Error));
    bc19.BCException = BCException;
    BCException["__class"] = "bc19.BCException";
    BCException["__interfaces"] = ["java.io.Serializable"];
})(bc19 || (bc19 = {}));
(function (bc19) {
    var BCAbstractRobot = (function () {
        function BCAbstractRobot() {
            this.SPECS = null;
            this.gameState = null;
            this.logs = null;
            this.__signal = 0;
            this.signalRadius = 0;
            this.__castleTalk = 0;
            this.me = null;
            this.id = 0;
            this.fuel = 0;
            this.karbonite = 0;
            this.lastOffer = null;
            this.map = null;
            this.karboniteMap = null;
            this.fuelMap = null;
            this.resetState();
        }
        BCAbstractRobot.prototype.setSpecs = function (specs) {
            this.SPECS = specs;
        };
        /*private*/ BCAbstractRobot.prototype.resetState = function () {
            this.logs = ([]);
            this.__signal = 0;
            this.signalRadius = 0;
            this.__castleTalk = 0;
        };
        BCAbstractRobot.prototype._do_turn = function (gameState) {
            this.gameState = gameState;
            this.id = gameState.id;
            this.karbonite = gameState.karbonite;
            this.fuel = gameState.fuel;
            this.lastOffer = gameState.last_offer;
            this.me = this.getRobot(this.id);
            if (this.me.turn === 1) {
                this.map = gameState.map;
                this.karboniteMap = gameState.karbonite_map;
                this.fuelMap = gameState.fuel_map;
            }
            var t = null;
            try {
                t = this.turn();
            }
            catch (e) {
                t = new bc19.ErrorAction(e, this.__signal, this.signalRadius, this.logs, this.__castleTalk);
            }
            ;
            if (t == null)
                t = new bc19.Action(this.__signal, this.signalRadius, this.logs, this.__castleTalk);
            t.signal = this.__signal;
            t.signal_radius = this.signalRadius;
            t.logs = this.logs;
            t.castle_talk = this.__castleTalk;
            this.resetState();
            return t;
        };
        /*private*/ BCAbstractRobot.prototype.checkOnMap = function (x, y) {
            return x >= 0 && x < this.gameState.shadow[0].length && y >= 0 && y < this.gameState.shadow.length;
        };
        BCAbstractRobot.prototype.log = function (message) {
            /* add */ (this.logs.push(message) > 0);
        };
        BCAbstractRobot.prototype.signal = function (value, radius) {
            if (this.fuel < radius)
                throw new bc19.BCException("Not enough fuel to signal given radius.");
            if (value < 0 || value >= Math.pow(2, this.SPECS.COMMUNICATION_BITS))
                throw new bc19.BCException("Invalid signal, must be within bit range.");
            if (radius > 2 * Math.pow(this.SPECS.MAX_BOARD_SIZE - 1, 2))
                throw new bc19.BCException("Signal radius is too big.");
            this.__signal = value;
            this.signalRadius = radius;
            this.fuel -= radius;
        };
        BCAbstractRobot.prototype.castleTalk = function (value) {
            if (value < 0 || value >= Math.pow(2, this.SPECS.CASTLE_TALK_BITS))
                throw new bc19.BCException("Invalid castle talk, must be between 0 and 2^8.");
            this.__castleTalk = value;
        };
        BCAbstractRobot.prototype.proposeTrade = function (k, f) {
            if (this.me.unit !== this.SPECS.CASTLE)
                throw new bc19.BCException("Only castles can trade.");
            if (Math.abs(k) >= this.SPECS.MAX_TRADE || Math.abs(f) >= this.SPECS.MAX_TRADE)
                throw new bc19.BCException("Cannot trade over " + ('' + (this.SPECS.MAX_TRADE)) + " in a given turn.");
            return new bc19.TradeAction(f, k, this.__signal, this.signalRadius, this.logs, this.__castleTalk);
        };
        BCAbstractRobot.prototype.buildUnit = function (unit, dx, dy) {
            if (this.me.unit !== this.SPECS.PILGRIM && this.me.unit !== this.SPECS.CASTLE && this.me.unit !== this.SPECS.CHURCH)
                throw new bc19.BCException("This unit type cannot build.");
            if (this.me.unit === this.SPECS.PILGRIM && unit !== this.SPECS.CHURCH)
                throw new bc19.BCException("Pilgrims can only build churches.");
            if (this.me.unit !== this.SPECS.PILGRIM && unit === this.SPECS.CHURCH)
                throw new bc19.BCException("Only pilgrims can build churches.");
            if (dx < -1 || dy < -1 || dx > 1 || dy > 1)
                throw new bc19.BCException("Can only build in adjacent squares.");
            if (!this.checkOnMap(this.me.x + dx, this.me.y + dy))
                throw new bc19.BCException("Can\'t build units off of map.");
            if (this.gameState.shadow[this.me.y + dy][this.me.x + dx] !== 0)
                throw new bc19.BCException("Cannot build on occupied tile.");
            if (!this.map[this.me.y + dy][this.me.x + dx])
                throw new bc19.BCException("Cannot build onto impassable terrain.");
            if (this.karbonite < this.SPECS.UNITS[unit].CONSTRUCTION_KARBONITE || this.fuel < this.SPECS.UNITS[unit].CONSTRUCTION_FUEL)
                throw new bc19.BCException("Cannot afford to build specified unit.");
            return new bc19.BuildAction(unit, dx, dy, this.__signal, this.signalRadius, this.logs, this.__castleTalk);
        };
        BCAbstractRobot.prototype.move = function (dx, dy) {
            if (this.me.unit === this.SPECS.CASTLE || this.me.unit === this.SPECS.CHURCH)
                throw new bc19.BCException("Churches and Castles cannot move.");
            if (!this.checkOnMap(this.me.x + dx, this.me.y + dy))
                throw new bc19.BCException("Can\'t move off of map.");
            if (this.gameState.shadow[this.me.y + dy][this.me.x + dx] === -1)
                throw new bc19.BCException("Cannot move outside of vision range.");
            if (this.gameState.shadow[this.me.y + dy][this.me.x + dx] !== 0)
                throw new bc19.BCException("Cannot move onto occupied tile.");
            if (!this.map[this.me.y + dy][this.me.x + dx])
                throw new bc19.BCException("Cannot move onto impassable terrain.");
            var r = dx * dx + dy * dy;
            if (r > this.SPECS.UNITS[this.me.unit].SPEED)
                throw new bc19.BCException("Slow down, cowboy.  Tried to move faster than unit can.");
            if (this.fuel < r * this.SPECS.UNITS[this.me.unit].FUEL_PER_MOVE)
                throw new bc19.BCException("Not enough fuel to move at given speed.");
            return new bc19.MoveAction(dx, dy, this.__signal, this.signalRadius, this.logs, this.__castleTalk);
        };
        BCAbstractRobot.prototype.mine = function () {
            if (this.me.unit !== this.SPECS.PILGRIM)
                throw new bc19.BCException("Only Pilgrims can mine.");
            if (this.fuel < this.SPECS.MINE_FUEL_COST)
                throw new bc19.BCException("Not enough fuel to mine.");
            if (this.karboniteMap[this.me.y][this.me.x]) {
                if (this.me.karbonite >= this.SPECS.UNITS[this.SPECS.PILGRIM].KARBONITE_CAPACITY)
                    throw new bc19.BCException("Cannot mine, as at karbonite capacity.");
            }
            else if (this.fuelMap[this.me.y][this.me.x]) {
                if (this.me.fuel >= this.SPECS.UNITS[this.SPECS.PILGRIM].FUEL_CAPACITY)
                    throw new bc19.BCException("Cannot mine, as at fuel capacity.");
            }
            else
                throw new bc19.BCException("Cannot mine square without fuel or karbonite.");
            return new bc19.MineAction(this.__signal, this.signalRadius, this.logs, this.__castleTalk);
        };
        BCAbstractRobot.prototype.give = function (dx, dy, k, f) {
            if (dx > 1 || dx < -1 || dy > 1 || dy < -1 || (dx === 0 && dy === 0))
                throw new bc19.BCException("Can only give to adjacent squares.");
            if (!this.checkOnMap(this.me.x + dx, this.me.y + dy))
                throw new bc19.BCException("Can\'t give off of map.");
            if (this.gameState.shadow[this.me.y + dy][this.me.x + dx] <= 0)
                throw new bc19.BCException("Cannot give to empty square.");
            if (k < 0 || f < 0 || this.me.karbonite < k || this.me.fuel < f)
                throw new bc19.BCException("Do not have specified amount to give.");
            return new bc19.GiveAction(k, f, dx, dy, this.__signal, this.signalRadius, this.logs, this.__castleTalk);
        };
        BCAbstractRobot.prototype.attack = function (dx, dy) {
            if (this.me.unit !== this.SPECS.CRUSADER && this.me.unit !== this.SPECS.PREACHER && this.me.unit !== this.SPECS.PROPHET)
                throw new bc19.BCException("Given unit cannot attack.");
            if (this.fuel < this.SPECS.UNITS[this.me.unit].ATTACK_FUEL_COST)
                throw new bc19.BCException("Not enough fuel to attack.");
            if (!this.checkOnMap(this.me.x + dx, this.me.y + dy))
                throw new bc19.BCException("Can\'t attack off of map.");
            if (this.gameState.shadow[this.me.y + dy][this.me.x + dx] === -1)
                throw new bc19.BCException("Cannot attack outside of vision range.");
            if (!this.map[this.me.y + dy][this.me.x + dx])
                throw new bc19.BCException("Cannot attack impassable terrain.");
            if (this.gameState.shadow[this.me.y + dy][this.me.x + dx] === 0)
                throw new bc19.BCException("Cannot attack empty tile.");
            var r = dx * dx + dy * dy;
            if (r > this.SPECS.UNITS[this.me.unit].ATTACK_RADIUS[1] || r < this.SPECS.UNITS[this.me.unit].ATTACK_RADIUS[0])
                throw new bc19.BCException("Cannot attack outside of attack range.");
            return new bc19.AttackAction(dx, dy, this.__signal, this.signalRadius, this.logs, this.__castleTalk);
        };
        BCAbstractRobot.prototype.getRobot = function (id) {
            if (id <= 0)
                return null;
            for (var i = 0; i < this.gameState.visible.length; i++) {
                if (this.gameState.visible[i].id === id) {
                    return this.gameState.visible[i];
                }
            }
            ;
            return null;
        };
        BCAbstractRobot.prototype.isVisible = function (robot) {
            for (var x = 0; x < this.gameState.shadow[0].length; x++) {
                for (var y = 0; y < this.gameState.shadow.length; y++) {
                    if (robot.id === this.gameState.shadow[y][x])
                        return true;
                }
                ;
            }
            ;
            return false;
        };
        BCAbstractRobot.prototype.isRadioing = function (robot) {
            return robot.signal >= 0;
        };
        BCAbstractRobot.prototype.getVisibleRobotMap = function () {
            return this.gameState.shadow;
        };
        BCAbstractRobot.prototype.getPassableMap = function () {
            return this.map;
        };
        BCAbstractRobot.prototype.getKarboniteMap = function () {
            return this.karboniteMap;
        };
        BCAbstractRobot.prototype.getFuelMap = function () {
            return this.fuelMap;
        };
        BCAbstractRobot.prototype.getVisibleRobots = function () {
            return this.gameState.visible;
        };
        BCAbstractRobot.prototype.turn = function () {
            return null;
        };
        return BCAbstractRobot;
    }());
    bc19.BCAbstractRobot = BCAbstractRobot;
    BCAbstractRobot["__class"] = "bc19.BCAbstractRobot";
})(bc19 || (bc19 = {}));
(function (bc19) {
    var Church = (function () {
        function Church(robot) {
            this.robot = null;
            this.ourTeam = 0;
            this.location = null;
            this.mapIsHorizontal = false;
            this.robot = robot;
        }
        Church.prototype.Execute = function () {
            return this.robot.buildUnit(this.robot.SPECS.PILGRIM, 1, 0);
        };
        Church.prototype.InitializeVariables = function () {
            this.ourTeam = this.robot.me.team === this.robot.SPECS.RED ? 0 : 1;
            this.mapIsHorizontal = bc19.Helper.FindSymmetry(this.robot.map);
            this.location = new bc19.Position(this.robot.me.y, this.robot.me.x);
        };
        return Church;
    }());
    bc19.Church = Church;
    Church["__class"] = "bc19.Church";
    Church["__interfaces"] = ["bc19.Machine"];
})(bc19 || (bc19 = {}));
(function (bc19) {
    var MineAction = (function (_super) {
        __extends(MineAction, _super);
        function MineAction(signal, signalRadius, logs, castleTalk) {
            var _this = _super.call(this, signal, signalRadius, logs, castleTalk) || this;
            _this.action = null;
            _this.action = "mine";
            return _this;
        }
        return MineAction;
    }(bc19.Action));
    bc19.MineAction = MineAction;
    MineAction["__class"] = "bc19.MineAction";
})(bc19 || (bc19 = {}));
(function (bc19) {
    var ErrorAction = (function (_super) {
        __extends(ErrorAction, _super);
        function ErrorAction(error, signal, signalRadius, logs, castleTalk) {
            var _this = _super.call(this, signal, signalRadius, logs, castleTalk) || this;
            _this.error = null;
            _this.error = error.message;
            return _this;
        }
        return ErrorAction;
    }(bc19.Action));
    bc19.ErrorAction = ErrorAction;
    ErrorAction["__class"] = "bc19.ErrorAction";
})(bc19 || (bc19 = {}));
(function (bc19) {
    var GiveAction = (function (_super) {
        __extends(GiveAction, _super);
        function GiveAction(giveKarbonite, giveFuel, dx, dy, signal, signalRadius, logs, castleTalk) {
            var _this = _super.call(this, signal, signalRadius, logs, castleTalk) || this;
            _this.action = null;
            _this.give_karbonite = 0;
            _this.give_fuel = 0;
            _this.dx = 0;
            _this.dy = 0;
            _this.action = "give";
            _this.give_karbonite = giveKarbonite;
            _this.give_fuel = giveFuel;
            _this.dx = dx;
            _this.dy = dy;
            return _this;
        }
        return GiveAction;
    }(bc19.Action));
    bc19.GiveAction = GiveAction;
    GiveAction["__class"] = "bc19.GiveAction";
})(bc19 || (bc19 = {}));
(function (bc19) {
    var BuildAction = (function (_super) {
        __extends(BuildAction, _super);
        function BuildAction(buildUnit, dx, dy, signal, signalRadius, logs, castleTalk) {
            var _this = _super.call(this, signal, signalRadius, logs, castleTalk) || this;
            _this.action = null;
            _this.build_unit = 0;
            _this.dx = 0;
            _this.dy = 0;
            _this.action = "build";
            _this.build_unit = buildUnit;
            _this.dx = dx;
            _this.dy = dy;
            return _this;
        }
        return BuildAction;
    }(bc19.Action));
    bc19.BuildAction = BuildAction;
    BuildAction["__class"] = "bc19.BuildAction";
})(bc19 || (bc19 = {}));
(function (bc19) {
    var TradeAction = (function (_super) {
        __extends(TradeAction, _super);
        function TradeAction(trade_fuel, trade_karbonite, signal, signalRadius, logs, castleTalk) {
            var _this = _super.call(this, signal, signalRadius, logs, castleTalk) || this;
            _this.action = null;
            _this.trade_fuel = 0;
            _this.trade_karbonite = 0;
            _this.action = "trade";
            _this.trade_fuel = trade_fuel;
            _this.trade_karbonite = trade_karbonite;
            return _this;
        }
        return TradeAction;
    }(bc19.Action));
    bc19.TradeAction = TradeAction;
    TradeAction["__class"] = "bc19.TradeAction";
})(bc19 || (bc19 = {}));
(function (bc19) {
    var MoveAction = (function (_super) {
        __extends(MoveAction, _super);
        function MoveAction(dx, dy, signal, signalRadius, logs, castleTalk) {
            var _this = _super.call(this, signal, signalRadius, logs, castleTalk) || this;
            _this.action = null;
            _this.dx = 0;
            _this.dy = 0;
            _this.action = "move";
            _this.dx = dx;
            _this.dy = dy;
            return _this;
        }
        return MoveAction;
    }(bc19.Action));
    bc19.MoveAction = MoveAction;
    MoveAction["__class"] = "bc19.MoveAction";
})(bc19 || (bc19 = {}));
(function (bc19) {
    var AttackAction = (function (_super) {
        __extends(AttackAction, _super);
        function AttackAction(dx, dy, signal, signalRadius, logs, castleTalk) {
            var _this = _super.call(this, signal, signalRadius, logs, castleTalk) || this;
            _this.action = null;
            _this.dx = 0;
            _this.dy = 0;
            _this.action = "attack";
            _this.dx = dx;
            _this.dy = dy;
            return _this;
        }
        return AttackAction;
    }(bc19.Action));
    bc19.AttackAction = AttackAction;
    AttackAction["__class"] = "bc19.AttackAction";
})(bc19 || (bc19 = {}));
(function (bc19) {
    var Crusader = (function (_super) {
        __extends(Crusader, _super);
        function Crusader(robot) {
            var _this = _super.call(this) || this;
            _this.robot = null;
            _this.ourTeam = 0;
            _this.location = null;
            _this.mapIsHorizontal = false;
            _this.robot = robot;
            return _this;
        }
        Crusader.prototype.Execute = function () {
            return this.robot.move(0, 0);
        };
        Crusader.prototype.InitializeVariables = function () {
            this.ourTeam = this.robot.me.team === this.robot.SPECS.RED ? 0 : 1;
            this.mapIsHorizontal = bc19.Helper.FindSymmetry(this.robot.map);
            this.location = new bc19.Position(this.robot.me.y, this.robot.me.x);
        };
        return Crusader;
    }(bc19.MovingRobot));
    bc19.Crusader = Crusader;
    Crusader["__class"] = "bc19.Crusader";
    Crusader["__interfaces"] = ["bc19.Machine"];
})(bc19 || (bc19 = {}));
(function (bc19) {
    var Pilgrim = (function (_super) {
        __extends(Pilgrim, _super);
        function Pilgrim(robot) {
            var _this = _super.call(this) || this;
            _this.waitCounter = 0;
            _this.waitMax = 2;
            _this.emergencyAmount = 10;
            _this.karbThreshold = 100;
            _this.fuelThreshold = 500;
            _this.returnFuelThreshold = 22;
            _this.robot = null;
            _this.ourTeam = 0;
            _this.location = null;
            _this.mapIsHorizontal = false;
            _this.state = null;
            _this.initialized = false;
            _this.karbRoutes = null;
            _this.fuelRoutes = null;
            _this.ourDropOffRoutes = null;
            _this.dropOff = null;
            _this.maxKarb = 0;
            _this.maxFuel = 0;
            _this.miningKarb = false;
            _this.occupiedResources = null;
            _this.robot = robot;
            return _this;
        }
        Pilgrim.prototype.Execute = function () {
            if (!this.initialized) {
                this.Initialize();
            }
            else {
                this.UpdateOccupiedResources();
                if (this.state === bc19.PilgrimState.GoingToResource) {
                    this.GoToMine();
                }
                if (this.state === bc19.PilgrimState.Mining) {
                    this.Mining();
                }
                if (this.state === bc19.PilgrimState.Returning) {
                    this.ReturnToDropOff();
                }
            }
            return this.robot.move(0, 1);
        };
        Pilgrim.prototype.InitializeVariables = function () {
            this.ourTeam = this.robot.me.team === this.robot.SPECS.RED ? 0 : 1;
            this.mapIsHorizontal = bc19.Helper.FindSymmetry(this.robot.map);
            this.karbRoutes = ({});
            this.fuelRoutes = ({});
            this.ourDropOffRoutes = ({});
            this.location = new bc19.Position(this.robot.me.y, this.robot.me.x);
            this.state = bc19.PilgrimState.Initializing;
            this.maxKarb = 0;
            this.maxFuel = this.robot.SPECS.UNITS[this.robot.me.unit].FUEL_CAPACITY;
            this.miningKarb = true;
            this.occupiedResources = (function (dims) { var allocate = function (dims) { if (dims.length == 0) {
                return 0;
            }
            else {
                var array = [];
                for (var i = 0; i < dims[0]; i++) {
                    array.push(allocate(dims.slice(1)));
                }
                return array;
            } }; return allocate(dims); })([this.robot.map.length, this.robot.map[0].length]);
            for (var i = 0; i < this.robot.map.length; i++) {
                for (var j = 0; j < this.robot.map[0].length; j++) {
                    if (this.robot.getKarboniteMap()[i][j] === true || this.robot.getFuelMap()[i][j] === true) {
                        this.occupiedResources[i][j] = 0;
                    }
                    else {
                        this.occupiedResources[i][j] = -1;
                    }
                }
                ;
            }
            ;
        };
        Pilgrim.prototype.Initialize = function () {
        };
        Pilgrim.prototype.ReturnToDropOff = function () {
            if ((this.dropOff.x - this.location.x) * (this.dropOff.x - this.location.x) > 1 || (this.dropOff.y - this.location.y) * (this.dropOff.y - this.location.y) > 1) {
                return this.FloodPathing(this.robot, /* get */ (function (m, k) { if (m.entries == null)
                    m.entries = []; for (var i = 0; i < m.entries.length; i++)
                    if (m.entries[i].key.equals != null && m.entries[i].key.equals(k) || m.entries[i].key === k) {
                        return m.entries[i].value;
                    } return null; })(this.ourDropOffRoutes, this.dropOff));
            }
            this.state = bc19.PilgrimState.Dropoff;
            this.WhatToMine();
            return this.robot.give(this.dropOff.x - this.location.x, this.dropOff.y - this.location.y, this.robot.me.karbonite, this.robot.me.fuel);
        };
        Pilgrim.prototype.FuelToReturn = function (path) {
            var tilesFromTarget = path[this.location.y][this.location.x];
            var amountOfMoves = Math.fround((tilesFromTarget / Math.sqrt(this.robot.SPECS.UNITS[this.robot.SPECS.PILGRIM].SPEED)));
            return (Math.fround(amountOfMoves * this.robot.SPECS.UNITS[this.robot.SPECS.PILGRIM].FUEL_PER_MOVE));
        };
        Pilgrim.prototype.GetNearestResource = function () {
            var chosenRoute = this.miningKarb ? this.karbRoutes : this.fuelRoutes;
            var lowest = Number.MAX_VALUE;
            var closest = null;
            {
                var array138 = (function (m) { if (m.entries == null)
                    m.entries = []; return m.entries; })(chosenRoute);
                for (var index137 = 0; index137 < array138.length; index137++) {
                    var pair = array138[index137];
                    {
                        var distance = pair.getValue()[this.location.y][this.location.x];
                        if (this.occupiedResources[pair.getKey().y][pair.getKey().x] !== 1 && distance < lowest) {
                            lowest = distance;
                            closest = pair.getKey();
                        }
                    }
                }
            }
            return closest;
        };
        Pilgrim.prototype.GetNearestDropOff = function () {
            var lowest = Number.MAX_VALUE;
            {
                var array140 = (function (m) { var r = []; if (m.entries == null)
                    m.entries = []; for (var i = 0; i < m.entries.length; i++)
                    r.push(m.entries[i].key); return r; })(this.ourDropOffRoutes);
                for (var index139 = 0; index139 < array140.length; index139++) {
                    var dropOffPosition = array140[index139];
                    {
                        var distance = bc19.Helper.DistanceSquared(dropOffPosition, this.location);
                        if (distance < lowest) {
                            this.dropOff = dropOffPosition;
                            lowest = distance;
                        }
                    }
                }
            }
        };
        Pilgrim.prototype.UpdateOccupiedResources = function () {
            var visionRadius = (Math.sqrt(this.robot.SPECS.UNITS[this.robot.me.unit].VISION_RADIUS) | 0);
            for (var i = -visionRadius; i < visionRadius; i++) {
                for (var j = -visionRadius; j < visionRadius; j++) {
                    var yNew = this.robot.me.y + i;
                    var xNew = this.robot.me.x + i;
                    var tile = new bc19.Position(yNew, xNew);
                    if (bc19.Helper.DistanceSquared(tile, this.location) > this.robot.SPECS.UNITS[this.robot.SPECS.PILGRIM].VISION_RADIUS) {
                        continue;
                    }
                    if (bc19.Helper.RobotAtPosition(this.robot, tile) == null) {
                        this.occupiedResources[yNew][xNew] = 0;
                    }
                    else if (bc19.Helper.RobotAtPosition(this.robot, tile).unit === this.robot.SPECS.PILGRIM) {
                        this.occupiedResources[yNew][xNew] = 1;
                    }
                    else {
                        this.occupiedResources[yNew][xNew] = 2;
                    }
                }
                ;
            }
            ;
        };
        Pilgrim.prototype.GoToMine = function () {
            var nearest = this.GetNearestResource();
            var movespeed = (this.robot.SPECS.UNITS[this.robot.me.unit].SPEED | 0);
            if (nearest.y - this.location.y === 0 && nearest.x - this.location.x === 0) {
                this.state = bc19.PilgrimState.Mining;
                return this.robot.mine();
            }
            else if (bc19.Helper.DistanceSquared(nearest, this.location) < movespeed) {
                if (this.occupiedResources[nearest.y][nearest.x] === 2) {
                    this.waitCounter++;
                    if (this.waitCounter >= this.waitMax) {
                        this.occupiedResources[nearest.y][nearest.x] = 1;
                        this.GoToMine();
                    }
                    return null;
                }
                return this.robot.move(nearest.x - this.location.x, nearest.y - this.location.y);
            }
            else {
                this.state = bc19.PilgrimState.GoingToResource;
                if (this.miningKarb === true) {
                    return this.FloodPathing(this.robot, /* get */ (function (m, k) { if (m.entries == null)
                        m.entries = []; for (var i = 0; i < m.entries.length; i++)
                        if (m.entries[i].key.equals != null && m.entries[i].key.equals(k) || m.entries[i].key === k) {
                            return m.entries[i].value;
                        } return null; })(this.karbRoutes, nearest));
                }
                else {
                    return this.FloodPathing(this.robot, /* get */ (function (m, k) { if (m.entries == null)
                        m.entries = []; for (var i = 0; i < m.entries.length; i++)
                        if (m.entries[i].key.equals != null && m.entries[i].key.equals(k) || m.entries[i].key === k) {
                            return m.entries[i].value;
                        } return null; })(this.fuelRoutes, nearest));
                }
            }
        };
        Pilgrim.prototype.WhatToMine = function () {
            if (this.maxKarb === this.emergencyAmount) {
                this.miningKarb = true;
            }
            else if (this.robot.karbonite < this.karbThreshold) {
                this.miningKarb = true;
            }
            else if (this.robot.fuel < this.fuelThreshold) {
                this.miningKarb = false;
            }
        };
        Pilgrim.prototype.Mining = function () {
            if (this.occupiedResources[this.location.y][this.location.x] === -1) {
                this.state = bc19.PilgrimState.GoingToResource;
            }
            var max = this.miningKarb ? this.maxKarb : this.maxFuel;
            var current = this.miningKarb ? this.robot.me.karbonite : this.robot.me.fuel;
            if (current >= max) {
                this.state = bc19.PilgrimState.Returning;
                var church = this.BuildChurch();
                return church == null ? this.ReturnToDropOff() : church;
            }
            else {
                return this.robot.mine();
            }
        };
        Pilgrim.prototype.ShouldBuildChurch = function () {
            if (this.maxKarb === this.emergencyAmount) {
                return false;
            }
            this.GetNearestDropOff();
            if (this.FuelToReturn(/* get */ (function (m, k) { if (m.entries == null)
                m.entries = []; for (var i = 0; i < m.entries.length; i++)
                if (m.entries[i].key.equals != null && m.entries[i].key.equals(k) || m.entries[i].key === k) {
                    return m.entries[i].value;
                } return null; })(this.ourDropOffRoutes, this.dropOff)) > this.returnFuelThreshold) {
                return true;
            }
            else {
                return false;
            }
        };
        Pilgrim.prototype.BuildChurch = function () {
            var buildChurchHere = bc19.Helper.RandomNonResourceAdjacentPosition(this.robot, this.location);
            var dx = buildChurchHere.x - this.location.x;
            var dy = buildChurchHere.y - this.location.y;
            if (this.ShouldBuildChurch() === true) {
                this.state = bc19.PilgrimState.Returning;
                return this.robot.buildUnit(this.robot.SPECS.CHURCH, dx, dy);
            }
            return null;
        };
        return Pilgrim;
    }(bc19.MovingRobot));
    bc19.Pilgrim = Pilgrim;
    Pilgrim["__class"] = "bc19.Pilgrim";
    Pilgrim["__interfaces"] = ["bc19.Machine"];
    var PilgrimState;
    (function (PilgrimState) {
        PilgrimState[PilgrimState["Initializing"] = 0] = "Initializing";
        PilgrimState[PilgrimState["GoingToResource"] = 1] = "GoingToResource";
        PilgrimState[PilgrimState["Mining"] = 2] = "Mining";
        PilgrimState[PilgrimState["Returning"] = 3] = "Returning";
        PilgrimState[PilgrimState["Dropoff"] = 4] = "Dropoff";
    })(PilgrimState = bc19.PilgrimState || (bc19.PilgrimState = {}));
})(bc19 || (bc19 = {}));
(function (bc19) {
    var Preacher = (function (_super) {
        __extends(Preacher, _super);
        function Preacher(robot) {
            var _this = _super.call(this) || this;
            _this.robot = null;
            _this.ourTeam = 0;
            _this.location = null;
            _this.initialized = false;
            _this.mapIsHorizontal = false;
            _this.castleLocations = null;
            _this.closestCastle = null;
            _this.robot = robot;
            return _this;
        }
        Preacher.prototype.Execute = function () {
            if (this.robot.me.turn === 1) {
                this.castleLocations = ([]);
            }
            if (!this.initialized) {
                this.initialized = this.ReadInitialSignals(this.robot, this.castleLocations);
            }
            return null;
        };
        Preacher.prototype.Initialize = function () {
            if (this.robot.me.turn === 1) {
                this.InitializeVariables();
            }
            if (!this.initialized) {
                this.initialized = this.ReadInitialSignals(this.robot, this.castleLocations);
            }
        };
        Preacher.prototype.InitializeVariables = function () {
            this.ourTeam = this.robot.me.team === this.robot.SPECS.RED ? 0 : 1;
            this.mapIsHorizontal = bc19.Helper.FindSymmetry(this.robot.map);
            this.location = new bc19.Position(this.robot.me.y, this.robot.me.x);
            this.GetClosestCastle();
        };
        Preacher.prototype.AttackClosest = function () {
            var visibleRobots = this.robot.getVisibleRobots();
            var leastDistance = Number.MAX_VALUE;
            var closestIndex = -1;
            for (var i = 0; i < visibleRobots.length; i++) {
                if (visibleRobots[i].team !== this.ourTeam) {
                    var distance = bc19.Helper.DistanceSquared(new bc19.Position(visibleRobots[i].y, visibleRobots[i].x), this.location);
                    if (distance < leastDistance) {
                        leastDistance = distance;
                        closestIndex = i;
                    }
                }
            }
            ;
            return this.robot.attack(visibleRobots[closestIndex].y - this.location.y, visibleRobots[closestIndex].x - this.location.x);
        };
        Preacher.prototype.GetClosestCastle = function () {
            var least = Number.MAX_VALUE;
            for (var index141 = 0; index141 < this.castleLocations.length; index141++) {
                var castlePos = this.castleLocations[index141];
                {
                    var distance = bc19.Helper.DistanceSquared(castlePos, this.location);
                    if (distance < least) {
                        least = distance;
                        this.closestCastle = castlePos;
                    }
                }
            }
        };
        return Preacher;
    }(bc19.MovingRobot));
    bc19.Preacher = Preacher;
    Preacher["__class"] = "bc19.Preacher";
    Preacher["__interfaces"] = ["bc19.Machine"];
})(bc19 || (bc19 = {}));
(function (bc19) {
    var Prophet = (function (_super) {
        __extends(Prophet, _super);
        function Prophet(robot) {
            var _this = _super.call(this) || this;
            _this.robot = null;
            _this.initialized = false;
            _this.ourTeam = 0;
            _this.location = null;
            _this.mapIsHorizontal = false;
            _this.castleLocations = null;
            _this.robot = robot;
            return _this;
        }
        Prophet.prototype.Execute = function () {
            if (this.robot.me.turn === 1) {
                this.castleLocations = ([]);
            }
            if (!this.initialized) {
                this.initialized = this.ReadInitialSignals(this.robot, this.castleLocations);
            }
            return null;
        };
        Prophet.prototype.Initialize = function () {
            if (this.robot.me.turn === 1) {
                this.InitializeVariables();
            }
            if (!this.initialized) {
                this.initialized = this.ReadInitialSignals(this.robot, this.castleLocations);
            }
        };
        Prophet.prototype.InitializeVariables = function () {
            this.ourTeam = this.robot.me.team === this.robot.SPECS.RED ? 0 : 1;
            this.mapIsHorizontal = bc19.Helper.FindSymmetry(this.robot.map);
            this.location = new bc19.Position(this.robot.me.y, this.robot.me.x);
            this.castleLocations = ([]);
            this.initialized = false;
        };
        return Prophet;
    }(bc19.MovingRobot));
    bc19.Prophet = Prophet;
    Prophet["__class"] = "bc19.Prophet";
    Prophet["__interfaces"] = ["bc19.Machine"];
})(bc19 || (bc19 = {}));
(function (bc19) {
    var MyRobot = (function (_super) {
        __extends(MyRobot, _super);
        function MyRobot() {
            var _this = _super.call(this) || this;
            _this.debugTurn = 0;
            _this.robot = null;
            return _this;
        }
        MyRobot.prototype.turn = function () {
            this.debugTurn++;
            if (this.robot == null) {
                if (this.me.unit === this.SPECS.CASTLE) {
                    this.robot = new bc19.Castle(this);
                }
                else if (this.me.unit === this.SPECS.CHURCH) {
                    this.robot = new bc19.Church(this);
                }
                else if (this.me.unit === this.SPECS.PILGRIM) {
                    this.robot = new bc19.Pilgrim(this);
                }
                else if (this.me.unit === this.SPECS.CRUSADER) {
                    this.robot = new bc19.Crusader(this);
                }
                else if (this.me.unit === this.SPECS.PROPHET) {
                    this.robot = new bc19.Prophet(this);
                }
                else if (this.me.unit === this.SPECS.PREACHER) {
                    this.robot = new bc19.Preacher(this);
                }
            }
            if (this.debugTurn === 1) {
                for (var z = 0; z < 1; z++) {
                    var tester = new bc19.Position(30, 10);
                    var test = bc19.MovingRobot.CreateLayeredFloodPath(this.map, tester);
                    bc19.MovingRobot.UpdateFlood(this, this.map, test, 3, 10, true);
                    this.log(" " + z);
                }
                ;
            }
            if (this.debugTurn === 2) {
                this.log("Time : " + this.me.time);
            }
            return this.robot.Execute();
        };
        return MyRobot;
    }(bc19.BCAbstractRobot));
    bc19.MyRobot = MyRobot;
    MyRobot["__class"] = "bc19.MyRobot";
    var Position = (function () {
        function Position(y, x) {
            this.y = 0;
            this.x = 0;
            this.y = y;
            this.x = x;
        }
        Position.prototype.toString = function () {
            return ('' + (this.y)) + " " + ('' + (this.x));
        };
        Position.convertBinary = function (num) {
            var binary = (function (s) { var a = []; while (s-- > 0)
                a.push(0); return a; })(40);
            var index = 0;
            while ((num > 0)) {
                binary[index++] = num % 2;
                num = (num / 2 | 0);
            }
            ;
            var cat = "";
            for (var i = index - 1; i >= 0; i--) {
                cat += binary[i];
            }
            ;
            return cat;
        };
        return Position;
    }());
    bc19.Position = Position;
    Position["__class"] = "bc19.Position";
})(bc19 || (bc19 = {}));
//# sourceMappingURL=bundle.js.map
var specs = {"COMMUNICATION_BITS":16,"CASTLE_TALK_BITS":8,"MAX_ROUNDS":1000,"TRICKLE_FUEL":25,"INITIAL_KARBONITE":100,"INITIAL_FUEL":500,"MINE_FUEL_COST":1,"KARBONITE_YIELD":2,"FUEL_YIELD":10,"MAX_TRADE":1024,"MAX_BOARD_SIZE":64,"MAX_ID":4096,"CASTLE":0,"CHURCH":1,"PILGRIM":2,"CRUSADER":3,"PROPHET":4,"PREACHER":5,"RED":0,"BLUE":1,"CHESS_INITIAL":100,"CHESS_EXTRA":20,"TURN_MAX_TIME":200,"MAX_MEMORY":50000000,"UNITS":[{"CONSTRUCTION_KARBONITE":null,"CONSTRUCTION_FUEL":null,"KARBONITE_CAPACITY":null,"FUEL_CAPACITY":null,"SPEED":0,"FUEL_PER_MOVE":null,"STARTING_HP":100,"VISION_RADIUS":100,"ATTACK_DAMAGE":null,"ATTACK_RADIUS":null,"ATTACK_FUEL_COST":null,"DAMAGE_SPREAD":null},{"CONSTRUCTION_KARBONITE":50,"CONSTRUCTION_FUEL":200,"KARBONITE_CAPACITY":null,"FUEL_CAPACITY":null,"SPEED":0,"FUEL_PER_MOVE":null,"STARTING_HP":50,"VISION_RADIUS":100,"ATTACK_DAMAGE":null,"ATTACK_RADIUS":null,"ATTACK_FUEL_COST":null,"DAMAGE_SPREAD":null},{"CONSTRUCTION_KARBONITE":10,"CONSTRUCTION_FUEL":50,"KARBONITE_CAPACITY":20,"FUEL_CAPACITY":100,"SPEED":4,"FUEL_PER_MOVE":1,"STARTING_HP":10,"VISION_RADIUS":100,"ATTACK_DAMAGE":null,"ATTACK_RADIUS":null,"ATTACK_FUEL_COST":null,"DAMAGE_SPREAD":null},{"CONSTRUCTION_KARBONITE":20,"CONSTRUCTION_FUEL":50,"KARBONITE_CAPACITY":20,"FUEL_CAPACITY":100,"SPEED":9,"FUEL_PER_MOVE":1,"STARTING_HP":40,"VISION_RADIUS":36,"ATTACK_DAMAGE":10,"ATTACK_RADIUS":[1,16],"ATTACK_FUEL_COST":10,"DAMAGE_SPREAD":0},{"CONSTRUCTION_KARBONITE":25,"CONSTRUCTION_FUEL":50,"KARBONITE_CAPACITY":20,"FUEL_CAPACITY":100,"SPEED":4,"FUEL_PER_MOVE":2,"STARTING_HP":20,"VISION_RADIUS":64,"ATTACK_DAMAGE":10,"ATTACK_RADIUS":[16,64],"ATTACK_FUEL_COST":25,"DAMAGE_SPREAD":0},{"CONSTRUCTION_KARBONITE":30,"CONSTRUCTION_FUEL":50,"KARBONITE_CAPACITY":20,"FUEL_CAPACITY":100,"SPEED":4,"FUEL_PER_MOVE":3,"STARTING_HP":60,"VISION_RADIUS":16,"ATTACK_DAMAGE":20,"ATTACK_RADIUS":[1,16],"ATTACK_FUEL_COST":15,"DAMAGE_SPREAD":3}]};
var robot = new bc19.MyRobot(); robot.setSpecs(specs);