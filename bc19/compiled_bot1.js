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
        MovingRobot.CreateLayeredFloodPath = function (map, startPos, endPos) {
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
            /* add */ (toBeVisited.push(new bc19.PathingPosition(startPos, 0)) > 0);
            while ((toBeVisited.length > 0)) {
                var removed = (function (a) { return a.length == 0 ? null : a.shift(); })(toBeVisited);
                var cum = removed.cumulative;
                var down = new bc19.Position(removed.pos.y + 1, removed.pos.x);
                var up = new bc19.Position(removed.pos.y - 1, removed.pos.x);
                var right = new bc19.Position(removed.pos.y, removed.pos.x + 1);
                var left = new bc19.Position(removed.pos.y, removed.pos.x - 1);
                if (bc19.Helper.inMap(map, down) && singleStep[down.y][down.x] > 0 && (Math.fround(removed.cumulative - singleStep[down.y][down.x])) > 1) {
                    cum = Math.fround(singleStep[down.y][down.x] + 1);
                }
                else if (bc19.Helper.inMap(map, up) && singleStep[up.y][up.x] > 0 && (Math.fround(removed.cumulative - singleStep[up.y][up.x])) > 1) {
                    cum = Math.fround(singleStep[up.y][up.x] + 1);
                }
                else if (bc19.Helper.inMap(map, right) && singleStep[right.y][right.x] > 0 && (Math.fround(removed.cumulative - singleStep[right.y][right.x])) > 1) {
                    cum = Math.fround(singleStep[right.y][right.x] + 1);
                }
                else if (bc19.Helper.inMap(map, left) && singleStep[left.y][left.x] > 0 && (Math.fround(removed.cumulative - singleStep[left.y][left.x])) > 1) {
                    cum = Math.fround(singleStep[left.y][left.x] + 1);
                }
                singleStep[removed.pos.y][removed.pos.x] = map[removed.pos.y][removed.pos.x] ? cum : -1;
                if (removed.pos.y === endPos.y && removed.pos.x === endPos.x) {
                    return singleStep;
                }
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
            singleStep[startPos.y][startPos.x] = 0;
            return singleStep;
        };
        MovingRobot.prototype.ClosestEnemyCastle = function (robot, maps) {
            var lowest = Number.MAX_VALUE;
            var output = null;
            {
                var array122 = (function (m) { if (m.entries == null)
                    m.entries = []; return m.entries; })(maps);
                for (var index121 = 0; index121 < array122.length; index121++) {
                    var entry = array122[index121];
                    {
                        if (entry.getValue()[robot.me.y][robot.me.x] < lowest) {
                            lowest = entry.getValue()[robot.me.y][robot.me.x];
                            output = entry.getKey();
                        }
                    }
                }
            }
            return output;
        };
        MovingRobot.prototype.UpgradeMaps = function (robot, maps, upgradedMaps) {
            if (robot.me.time > 200) {
                if (upgradedMaps.length > 0) {
                    /* put */ (function (m, k, v) { if (m.entries == null)
                        m.entries = []; for (var i = 0; i < m.entries.length; i++)
                        if (m.entries[i].key.equals != null && m.entries[i].key.equals(k) || m.entries[i].key === k) {
                            m.entries[i].value = v;
                            return;
                        } m.entries.push({ key: k, value: v, getKey: function () { return this.key; }, getValue: function () { return this.value; } }); })(maps, /* get */ upgradedMaps[upgradedMaps.length - 1], MovingRobot.CreateLayeredFloodPath(robot.map, /* get */ upgradedMaps[upgradedMaps.length - 1], new bc19.Position(200, 200)));
                    /* remove */ (function (a) { return a.splice(a.indexOf(/* get */ upgradedMaps[upgradedMaps.length - 1]), 1); })(upgradedMaps);
                }
                else {
                    {
                        var array124 = (function (m) { if (m.entries == null)
                            m.entries = []; return m.entries; })(maps);
                        for (var index123 = 0; index123 < array124.length; index123++) {
                            var entry = array124[index123];
                            {
                                var tileDistance = robot.SPECS.UNITS[robot.me.unit].SPEED;
                                /* put */ (function (m, k, v) { if (m.entries == null)
                                    m.entries = []; for (var i = 0; i < m.entries.length; i++)
                                    if (m.entries[i].key.equals != null && m.entries[i].key.equals(k) || m.entries[i].key === k) {
                                        m.entries[i].value = v;
                                        return;
                                    } m.entries.push({ key: k, value: v, getKey: function () { return this.key; }, getValue: function () { return this.value; } }); })(maps, entry.getKey(), MovingRobot.UpdateFlood(robot, robot.map, entry.getValue(), tileDistance, 8, true));
                            }
                        }
                    }
                    return true;
                }
            }
            return false;
        };
        MovingRobot.prototype.GetOrCreateMap = function (robot, maps, pos) {
            if ((function (m, k) { if (m.entries == null)
                m.entries = []; for (var i = 0; i < m.entries.length; i++)
                if (m.entries[i].key.equals != null && m.entries[i].key.equals(k) || m.entries[i].key === k) {
                    return true;
                } return false; })(maps, pos)) {
                if ((function (m, k) { if (m.entries == null)
                    m.entries = []; for (var i = 0; i < m.entries.length; i++)
                    if (m.entries[i].key.equals != null && m.entries[i].key.equals(k) || m.entries[i].key === k) {
                        return m.entries[i].value;
                    } return null; })(maps, pos)[pos.y][pos.x] <= 0) {
                    var newMap = MovingRobot.CreateLayeredFloodPath(robot.map, pos, new bc19.Position(robot.me.y, robot.me.x));
                    /* put */ (function (m, k, v) { if (m.entries == null)
                        m.entries = []; for (var i = 0; i < m.entries.length; i++)
                        if (m.entries[i].key.equals != null && m.entries[i].key.equals(k) || m.entries[i].key === k) {
                            m.entries[i].value = v;
                            return;
                        } m.entries.push({ key: k, value: v, getKey: function () { return this.key; }, getValue: function () { return this.value; } }); })(maps, pos, newMap);
                    return newMap;
                }
                else {
                    return (function (m, k) { if (m.entries == null)
                        m.entries = []; for (var i = 0; i < m.entries.length; i++)
                        if (m.entries[i].key.equals != null && m.entries[i].key.equals(k) || m.entries[i].key === k) {
                            return m.entries[i].value;
                        } return null; })(maps, pos);
                }
            }
            else {
                var newMap = MovingRobot.CreateLayeredFloodPath(robot.map, pos, new bc19.Position(robot.me.y, robot.me.x));
                /* put */ (function (m, k, v) { if (m.entries == null)
                    m.entries = []; for (var i = 0; i < m.entries.length; i++)
                    if (m.entries[i].key.equals != null && m.entries[i].key.equals(k) || m.entries[i].key === k) {
                        m.entries[i].value = v;
                        return;
                    } m.entries.push({ key: k, value: v, getKey: function () { return this.key; }, getValue: function () { return this.value; } }); })(maps, pos, newMap);
                return newMap;
            }
        };
        MovingRobot.prototype.PathingDistance = function (robot, path) {
            return path[robot.me.y][robot.me.x];
        };
        MovingRobot.prototype.FloodPathing = function (robot, path, goal) {
            if (path == null) {
                return null;
            }
            var moveSpeed = robot.SPECS.UNITS[robot.me.unit].SPEED;
            if (bc19.Helper.DistanceSquared(new bc19.Position(robot.me.y, robot.me.x), goal) <= moveSpeed) {
                if (robot.getVisibleRobotMap()[goal.y][goal.x] === 0) {
                    return robot.move(goal.x - robot.me.x, goal.y - robot.me.y);
                }
                var adj = bc19.Helper.RandomNonResourceAdjacentPositionInMoveRange(robot, goal);
                if (adj != null) {
                    return robot.move(adj.x - robot.me.x, adj.y - robot.me.y);
                }
                else {
                    return null;
                }
            }
            var validPositions = bc19.Helper.AllOpenInRange(robot, robot.map, new bc19.Position(robot.me.y, robot.me.x), robot.SPECS.UNITS[robot.me.unit].SPEED);
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
        MovingRobot.prototype.CombatFloodPathing = function (robot, path, goal, ourTeam) {
            if (path == null) {
                return null;
            }
            var moveSpeed = robot.SPECS.UNITS[robot.me.unit].SPEED;
            if (bc19.Helper.DistanceSquared(new bc19.Position(robot.me.y, robot.me.x), goal) <= moveSpeed) {
                if (robot.getVisibleRobotMap()[goal.y][goal.x] === 0) {
                    return robot.move(goal.x - robot.me.x, goal.y - robot.me.y);
                }
                var adj = bc19.Helper.RandomNonResourceAdjacentPositionInMoveRange(robot, goal);
                if (adj != null) {
                    return robot.move(adj.x - robot.me.x, adj.y - robot.me.y);
                }
                else {
                    return null;
                }
            }
            var validFormations = bc19.Helper.AllOpenInRangeInFormation(robot, robot.map, new bc19.Position(robot.me.y, robot.me.x), robot.SPECS.UNITS[robot.me.unit].SPEED, ourTeam);
            var validPositions = bc19.Helper.AllOpenInRange(robot, robot.map, new bc19.Position(robot.me.y, robot.me.x), robot.SPECS.UNITS[robot.me.unit].SPEED);
            var lowest = path[robot.me.y][robot.me.x];
            if (lowest <= 0) {
                lowest = Number.MAX_VALUE;
            }
            var lowestPos = null;
            for (var i = 0; i < validFormations.length; i++) {
                if (path[validFormations[i].y][validFormations[i].x] < lowest && path[validFormations[i].y][validFormations[i].x] > 0) {
                    lowest = path[validFormations[i].y][validFormations[i].x];
                    lowestPos = validFormations[i];
                }
            }
            ;
            if (lowestPos == null) {
                for (var i = 0; i < validPositions.length; i++) {
                    if (path[validPositions[i].y][validPositions[i].x] < lowest && path[validPositions[i].y][validPositions[i].x] > 0) {
                        lowest = path[validPositions[i].y][validPositions[i].x];
                        lowestPos = validPositions[i];
                    }
                }
                ;
            }
            if (lowestPos != null) {
                return robot.move(lowestPos.x - robot.me.x, lowestPos.y - robot.me.y);
            }
            else {
                return this.MoveCloser(robot, goal);
            }
        };
        MovingRobot.prototype.ReadInitialSignals = function (robot, castleLocations) {
            var outputRead = new Array(3);
            var spawnCastle = robot.me;
            {
                var array126 = robot.getVisibleRobots();
                for (var index125 = 0; index125 < array126.length; index125++) {
                    var r = array126[index125];
                    {
                        if (r.unit === robot.SPECS.CASTLE && bc19.Helper.DistanceSquared(new bc19.Position(robot.me.y, robot.me.x), new bc19.Position(r.y, r.x)) <= 3) {
                            spawnCastle = r;
                        }
                        else if (r.unit === robot.SPECS.CHURCH) {
                            /* add */ (castleLocations.push(new bc19.Position(r.y, r.x)) > 0);
                            outputRead[0] = true;
                            return outputRead;
                        }
                    }
                }
            }
            var spawnCastlePos = new bc19.Position(spawnCastle.y, spawnCastle.x);
            if (castleLocations.length === 0) {
                /* add */ (castleLocations.push(spawnCastlePos) > 0);
            }
            var signal = spawnCastle.signal;
            if (signal <= 0 || castleLocations.length === 3) {
                outputRead[0] = true;
                return outputRead;
            }
            var x = signal & 63;
            signal >>= 6;
            var y = signal & 63;
            signal >>= 6;
            var numCastle = signal & 3;
            signal >>= 2;
            outputRead[2] = (signal & 1) === 1 ? true : false;
            signal >>= 1;
            outputRead[1] = (signal & 1) === 1 ? true : false;
            if (numCastle === 1) {
                outputRead[0] = true;
                return outputRead;
            }
            if (numCastle === 2) {
                /* add */ (castleLocations.push(new bc19.Position(y, x)) > 0);
                outputRead[0] = true;
                return outputRead;
            }
            else {
                /* add */ (castleLocations.push(new bc19.Position(y, x)) > 0);
                outputRead[0] = false;
                return outputRead;
            }
        };
        MovingRobot.prototype.MoveCloser = function (robot, pos) {
            var moveSpeed = robot.SPECS.UNITS[robot.me.unit].SPEED;
            var tileDistance = (Math.sqrt(robot.SPECS.UNITS[robot.me.unit].SPEED) | 0);
            var closest = Number.MAX_VALUE;
            var output = null;
            for (var y = -tileDistance; y <= tileDistance; y++) {
                for (var x = -tileDistance; x <= tileDistance; x++) {
                    var possible = new bc19.Position(robot.me.y + y, robot.me.x + x);
                    if (bc19.Helper.inMap(robot.map, possible) && robot.map[possible.y][possible.x] && robot.getVisibleRobotMap()[possible.y][possible.x] === 0 && bc19.Helper.DistanceSquared(new bc19.Position(robot.me.y, robot.me.x), possible) <= moveSpeed) {
                        if (bc19.Helper.DistanceSquared(pos, possible) < closest) {
                            closest = bc19.Helper.DistanceSquared(pos, possible);
                            output = possible;
                        }
                    }
                }
                ;
            }
            ;
            if (output != null) {
                return robot.move(output.x - robot.me.x, output.y - robot.me.y);
            }
            return null;
        };
        MovingRobot.prototype.WatchForSignal = function (robot, signal) {
            var robots = robot.getVisibleRobots();
            for (var i = 0; i < robots.length; i++) {
                if (robots[i].signal === signal) {
                    return true;
                }
            }
            ;
            return false;
        };
        MovingRobot.prototype.EnemiesAround = function (robot, ourTeam) {
            var robots = robot.getVisibleRobots();
            for (var i = 0; i < robots.length; i++) {
                if (robots[i].team !== ourTeam) {
                    return true;
                }
            }
            ;
            return false;
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
            this.positionInSpawnOrder = 0;
            this.prophetCounter = 0;
            this.prophetsPerPreacher = 2;
            this.unitsRequiredToMobilize = 4;
            this.robot = null;
            this.mapIsHorizontal = false;
            this.ourTeam = 0;
            this.numCastles = 0;
            this.location = null;
            this.ourCastles = null;
            this.enemyCastles = null;
            this.idDone = 0;
            this.state = null;
            this.spawnOrder = null;
            this.robot = robot;
        }
        Castle.prototype.Execute = function () {
            if (!this.initialized) {
                this.Initialize();
            }
            if (this.initialized && this.state === bc19.CastleState.DisabledInitial) {
                var closest = this.ClosestCastleToKarb();
                if (closest.y === this.location.y && closest.x === this.location.x) {
                    this.state = bc19.CastleState.EnabledInitial;
                }
                else if (this.robot.me.turn > 10 && this.robot.karbonite > 50) {
                    this.state = bc19.CastleState.EnabledInitial;
                }
                else {
                    this.state = bc19.CastleState.DisabledInitial;
                }
            }
            if (this.state !== bc19.CastleState.DisabledInitial) {
                var Attacking = this.state === bc19.CastleState.Mobilizing ? true : false;
                var EmergencyMining = this.positionInSpawnOrder < this.spawnOrder.length ? true : false;
                if (this.positionInSpawnOrder < this.spawnOrder.length) {
                    var spawnPosition = bc19.Helper.RandomNonResourceAdjacentPosition(this.robot, this.location);
                    if (bc19.Helper.CanAfford(this.robot, this.spawnOrder[this.positionInSpawnOrder])) {
                        var robotType = this.spawnOrder[this.positionInSpawnOrder];
                        this.positionInSpawnOrder++;
                        this.DeclareAllyCastlePositions(Attacking, EmergencyMining, 5);
                        return this.robot.buildUnit(robotType, spawnPosition.x - this.location.x, spawnPosition.y - this.location.y);
                    }
                }
                else if (this.state === bc19.CastleState.EnabledInitial) {
                    this.state = bc19.CastleState.Fortifying;
                }
                if (this.state === bc19.CastleState.Fortifying) {
                    if (this.robot.karbonite > 50 && this.robot.fuel > 100) {
                        if (this.prophetCounter < this.prophetsPerPreacher && bc19.Helper.CanAfford(this.robot, this.robot.SPECS.PROPHET)) {
                            var adj = bc19.Helper.RandomNonResourceAdjacentPosition(this.robot, this.location);
                            this.prophetCounter++;
                            return this.robot.buildUnit(this.robot.SPECS.PROPHET, adj.x - this.location.x, adj.y - this.location.y);
                        }
                        else if (bc19.Helper.CanAfford(this.robot, this.robot.SPECS.PILGRIM)) {
                            var adj = bc19.Helper.RandomNonResourceAdjacentPosition(this.robot, this.location);
                            this.prophetCounter = 0;
                            return this.robot.buildUnit(this.robot.SPECS.PREACHER, adj.x - this.location.x, adj.y - this.location.y);
                        }
                    }
                    if (this.ReadyToAttack()) {
                        this.state = bc19.CastleState.Mobilizing;
                    }
                }
                if (this.state === bc19.CastleState.Mobilizing) {
                    if (this.robot.karbonite > 50 && this.robot.fuel > 250) {
                        if (this.prophetCounter < this.prophetsPerPreacher && bc19.Helper.CanAfford(this.robot, this.robot.SPECS.PROPHET)) {
                            var adj = bc19.Helper.RandomNonResourceAdjacentPosition(this.robot, this.location);
                            this.prophetCounter++;
                            return this.robot.buildUnit(this.robot.SPECS.PROPHET, adj.x - this.location.x, adj.y - this.location.y);
                        }
                        else if (bc19.Helper.CanAfford(this.robot, this.robot.SPECS.PREACHER)) {
                            var adj = bc19.Helper.RandomNonResourceAdjacentPosition(this.robot, this.location);
                            this.prophetCounter = 0;
                            return this.robot.buildUnit(this.robot.SPECS.PREACHER, adj.x - this.location.x, adj.y - this.location.y);
                        }
                    }
                }
                this.DeclareAllyCastlePositions(Attacking, EmergencyMining, 5);
            }
            return null;
        };
        Castle.prototype.Initialize = function () {
            if (this.robot.me.turn === 1) {
                this.InitializeVariables();
                this.state = bc19.CastleState.DisabledInitial;
            }
            if (!this.initialized) {
                this.initialized = this.SetupAllyCastles();
                this.FindEnemyCastles();
            }
        };
        Castle.prototype.InitializeVariables = function () {
            this.spawnOrder = [this.robot.SPECS.PILGRIM, this.robot.SPECS.PREACHER, this.robot.SPECS.PREACHER, this.robot.SPECS.PREACHER, this.robot.SPECS.PILGRIM];
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
                var array128 = (function (m) { var r = []; if (m.entries == null)
                    m.entries = []; for (var i = 0; i < m.entries.length; i++)
                    r.push(m.entries[i].value); return r; })(this.ourCastles);
                for (var index127 = 0; index127 < array128.length; index127++) {
                    var pos = array128[index127];
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
                var array130 = (function (m) { if (m.entries == null)
                    m.entries = []; return m.entries; })(this.ourCastles);
                for (var index129 = 0; index129 < array130.length; index129++) {
                    var entry = array130[index129];
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
            if (this.numCastles === 1) {
                this.robot.signal(this.BinarySignalsForInitialization(bit1, bit2, new bc19.Position(1, 1)), radius);
            }
            else if (this.numCastles === 2) {
                var other = null;
                {
                    var array132 = (function (m) { var r = []; if (m.entries == null)
                        m.entries = []; for (var i = 0; i < m.entries.length; i++)
                        r.push(m.entries[i].key); return r; })(this.ourCastles);
                    for (var index131 = 0; index131 < array132.length; index131++) {
                        var id = array132[index131];
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
                if (other != null && other.x >= 0 && other.y >= 0) {
                    this.robot.signal(this.BinarySignalsForInitialization(bit1, bit2, other), radius);
                }
            }
            else {
                if (this.idDone === 0) {
                    var other = new bc19.Position(-1, -1);
                    {
                        var array134 = (function (m) { var r = []; if (m.entries == null)
                            m.entries = []; for (var i = 0; i < m.entries.length; i++)
                            r.push(m.entries[i].key); return r; })(this.ourCastles);
                        for (var index133 = 0; index133 < array134.length; index133++) {
                            var id = array134[index133];
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
                    if (other != null && other.x >= 0 && other.y >= 0) {
                        this.robot.signal(this.BinarySignalsForInitialization(bit1, bit2, other), radius);
                    }
                }
                else {
                    var other = new bc19.Position(-1, -1);
                    {
                        var array136 = (function (m) { var r = []; if (m.entries == null)
                            m.entries = []; for (var i = 0; i < m.entries.length; i++)
                            r.push(m.entries[i].key); return r; })(this.ourCastles);
                        for (var index135 = 0; index135 < array136.length; index135++) {
                            var id = array136[index135];
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
                    if (other != null && other.x >= 0 && other.y >= 0) {
                        this.robot.signal(this.BinarySignalsForInitialization(bit1, bit2, other), radius);
                    }
                }
            }
        };
        Castle.prototype.BinarySignalsForInitialization = function (bit1, bit2, pos) {
            var output = bit1 ? 1 : 0;
            output <<= 1;
            output += bit2 ? 1 : 0;
            output <<= 2;
            output += this.numCastles;
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
                var array138 = (function (m) { var r = []; if (m.entries == null)
                    m.entries = []; for (var i = 0; i < m.entries.length; i++)
                    r.push(m.entries[i].value); return r; })(this.ourCastles);
                for (var index137 = 0; index137 < array138.length; index137++) {
                    var castlePos = array138[index137];
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
        Castle.prototype.ReadyToAttack = function () {
            var robots = this.robot.getVisibleRobots();
            var alliedFightingBots = 0;
            for (var i = 0; i < robots.length; i++) {
                var bot = robots[i];
                if (bot.team === this.ourTeam && (bot.unit === this.robot.SPECS.PREACHER || bot.unit === this.robot.SPECS.PROPHET)) {
                    alliedFightingBots++;
                }
            }
            ;
            if (alliedFightingBots >= this.unitsRequiredToMobilize) {
                return true;
            }
            return false;
        };
        return Castle;
    }());
    bc19.Castle = Castle;
    Castle["__class"] = "bc19.Castle";
    Castle["__interfaces"] = ["bc19.Machine"];
    var CastleState;
    (function (CastleState) {
        CastleState[CastleState["EnabledInitial"] = 0] = "EnabledInitial";
        CastleState[CastleState["DisabledInitial"] = 1] = "DisabledInitial";
        CastleState[CastleState["Mobilizing"] = 2] = "Mobilizing";
        CastleState[CastleState["Fortifying"] = 3] = "Fortifying";
    })(CastleState = bc19.CastleState || (bc19.CastleState = {}));
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
        Helper.AllOpenInRange = function (robot, map, pos, r) {
            var validPositions = ([]);
            for (var i = -r; i <= r; i++) {
                for (var j = -r; j <= r; j++) {
                    var y = (pos.y + i);
                    var x = (pos.x + j);
                    if (!Helper.inMap(map, new bc19.Position(y, x)) || robot.getVisibleRobotMap()[y][x] !== 0) {
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
        Helper.AllOpenInRangeInFormation = function (robot, map, pos, r, ourTeam) {
            var validPositions = ([]);
            for (var i = -r; i <= r; i++) {
                for (var j = -r; j <= r; j++) {
                    var y = (pos.y + i);
                    var x = (pos.x + j);
                    if (!Helper.inMap(map, new bc19.Position(y, x)) || robot.getVisibleRobotMap()[y][x] !== 0) {
                        continue;
                    }
                    var distanceSquared = (y - pos.y) * (y - pos.y) + (x - pos.x) * (x - pos.x);
                    if (distanceSquared > r) {
                        continue;
                    }
                    var valid = new bc19.Position(y, x);
                    if (Helper.IsSurroundingsOccupied(robot, robot.getVisibleRobotMap(), valid, ourTeam) > 1) {
                        continue;
                    }
                    /* add */ (validPositions.push(valid) > 0);
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
            return mapIsHorizontal ? new bc19.Position((map.length - 1) - ourCastle.y, ourCastle.x) : new bc19.Position(ourCastle.y, (map[0].length - 1) - ourCastle.x);
        };
        Helper.FindEnemyCastles = function (robot, mapIsHorizontal, ourCastles) {
            var outputs = ([]);
            for (var i = 0; i < ourCastles.length; i++) {
                /* add */ (outputs.push(Helper.FindEnemyCastle(robot.map, mapIsHorizontal, /* get */ ourCastles[i])) > 0);
            }
            ;
            return outputs;
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
        Helper.IsSurroundingsOccupied = function (robot, map, pos, ourTeam) {
            var highest = 0;
            for (var i = -2; i <= 2; i++) {
                for (var j = -2; j <= 2; j++) {
                    var numAllies = -1;
                    var relative = new bc19.Position(pos.y + i, pos.x + j);
                    var robots = robot.getVisibleRobots();
                    for (var k = -1; k <= 1; k++) {
                        for (var l = -1; l <= 1; l++) {
                            for (var m = 0; m < robots.length; m++) {
                                if (relative.y + k === robots[m].y && relative.x + l === robots[m].x && robots[m].team === ourTeam) {
                                    numAllies++;
                                }
                            }
                            ;
                        }
                        ;
                    }
                    ;
                    if (numAllies > highest) {
                        highest = numAllies;
                    }
                }
                ;
            }
            ;
            return highest;
        };
        Helper.RandomNonResourceAdjacentPositionInMoveRange = function (robot, pos) {
            var robots = robot.getVisibleRobotMap();
            var fuelMap = robot.getFuelMap();
            var karbMap = robot.getKarboniteMap();
            for (var i = -1; i <= 1; i++) {
                for (var j = -1; j <= 1; j++) {
                    var adjacent = new bc19.Position(pos.y + i, pos.x + j);
                    if (Helper.inMap(robot.map, adjacent) && robot.map[adjacent.y][adjacent.x] && Helper.DistanceSquared(new bc19.Position(robot.me.y, robot.me.x), adjacent) <= robot.SPECS.UNITS[robot.me.unit].SPEED) {
                        if (robot.map[adjacent.y][adjacent.x] && robots[adjacent.y][adjacent.x] === 0 && fuelMap[adjacent.y][adjacent.x] === false && karbMap[adjacent.y][adjacent.x] === false) {
                            return new bc19.Position(adjacent.y, adjacent.x);
                        }
                    }
                }
                ;
            }
            ;
            return null;
        };
        Helper.RandomNonResourceAdjacentPosition = function (robot, pos) {
            var robots = robot.getVisibleRobotMap();
            var fuelMap = robot.getFuelMap();
            var karbMap = robot.getKarboniteMap();
            for (var i = -1; i <= 1; i++) {
                for (var j = -1; j <= 1; j++) {
                    if (Helper.inMap(robot.map, new bc19.Position(pos.y + i, pos.x + j)) && robot.map[pos.y + i][pos.x + j]) {
                        if (robot.map[pos.y + i][pos.x + j] && robots[pos.y + i][pos.x + j] === 0 && fuelMap[pos.y + i][pos.x + j] === false && karbMap[pos.y + i][pos.x + j] === false) {
                            return new bc19.Position(pos.y + i, pos.x + j);
                        }
                    }
                }
                ;
            }
            ;
            return null;
        };
        Helper.CanAfford = function (robot, unit) {
            if (robot.karbonite > robot.SPECS.UNITS[unit].CONSTRUCTION_KARBONITE && robot.fuel > robot.SPECS.UNITS[unit].CONSTRUCTION_FUEL) {
                return true;
            }
            return false;
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
            this.robot = robot;
        }
        Church.prototype.Execute = function () {
            return null;
        };
        Church.prototype.ResourcesAround = function () {
            var visionRadius = (Math.sqrt(this.robot.SPECS.UNITS[this.robot.me.unit].VISION_RADIUS) | 0) - 5;
            var numResources = 0;
            for (var i = -visionRadius; i <= visionRadius; i++) {
                for (var j = -visionRadius; j <= visionRadius; j++)
                    if (bc19.Helper.inMap(this.robot.map, new bc19.Position(this.location.y + i, this.location.x + j)) && (this.robot.getFuelMap()[this.location.y + i][this.location.x + j] || this.robot.getKarboniteMap()[this.location.y + i][this.location.x + j])) {
                        numResources++;
                    }
                ;
            }
            ;
            return numResources;
        };
        Church.prototype.PilgrimsAround = function () {
            var robots = this.robot.getVisibleRobots();
            var numPilgrims = 0;
            for (var i = 0; i < robots.length; i++) {
                if (robots[i].unit === this.robot.SPECS.PILGRIM) {
                    numPilgrims++;
                }
            }
            ;
            return numPilgrims;
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
            _this.oportunityKarbLostThreshold = 10;
            _this.robot = null;
            _this.ourTeam = 0;
            _this.location = null;
            _this.mapIsHorizontal = false;
            _this.state = null;
            _this.initialized = false;
            _this.karbLocations = null;
            _this.karbRoutes = null;
            _this.fuelLocations = null;
            _this.fuelRoutes = null;
            _this.dropOffLocations = null;
            _this.ourDropOffRoutes = null;
            _this.maxKarb = 0;
            _this.maxFuel = 0;
            _this.miningKarb = false;
            _this.occupiedResources = null;
            _this.robot = robot;
            return _this;
        }
        Pilgrim.prototype.Execute = function () {
            this.location = new bc19.Position(this.robot.me.y, this.robot.me.x);
            if (!this.initialized) {
                if (!this.CheckForChurch()) {
                    this.Initialize();
                    if (this.initialized) {
                        this.state = bc19.PilgrimState.GoingToResource;
                    }
                }
                else {
                    this.InitializeVariables();
                    this.maxKarb = 20;
                    this.initialized = true;
                    if (this.initialized) {
                        this.state = bc19.PilgrimState.GoingToResource;
                    }
                }
            }
            else {
                this.UpdateOccupiedResources();
                if (this.EnemiesAround(this.robot, this.ourTeam)) {
                    return this.ReturnToDropOff();
                }
                if (this.state === bc19.PilgrimState.GoingToResource) {
                    return this.GoToMine();
                }
                if (this.state === bc19.PilgrimState.Mining) {
                    return this.Mining();
                }
                if (this.state === bc19.PilgrimState.Returning) {
                    this.CheckForChurch();
                    return this.ReturnToDropOff();
                }
            }
            return null;
        };
        Pilgrim.prototype.InitializeVariables = function () {
            this.ourTeam = this.robot.me.team === this.robot.SPECS.RED ? 0 : 1;
            this.mapIsHorizontal = bc19.Helper.FindSymmetry(this.robot.map);
            this.karbRoutes = ({});
            this.fuelRoutes = ({});
            this.karbLocations = ([]);
            this.fuelLocations = ([]);
            this.dropOffLocations = ([]);
            this.ourDropOffRoutes = ({});
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
                    if (this.robot.getKarboniteMap()[i][j] === true) {
                        /* add */ (this.karbLocations.push(new bc19.Position(i, j)) > 0);
                        this.occupiedResources[i][j] = 0;
                    }
                    else if (this.robot.getFuelMap()[i][j] === true) {
                        /* add */ (this.fuelLocations.push(new bc19.Position(i, j)) > 0);
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
            if (this.robot.me.turn === 1) {
                this.InitializeVariables();
            }
            if (!this.initialized) {
                var signals = this.ReadInitialSignals(this.robot, this.dropOffLocations);
                this.initialized = signals[0];
                this.maxKarb = signals[2] ? this.emergencyAmount : this.robot.SPECS.UNITS[this.robot.me.unit].KARBONITE_CAPACITY;
            }
        };
        Pilgrim.prototype.UpdateOccupiedResources = function () {
            var visionRadius = (Math.sqrt(this.robot.SPECS.UNITS[this.robot.me.unit].VISION_RADIUS) | 0);
            for (var i = -visionRadius; i <= visionRadius; i++) {
                for (var j = -visionRadius; j <= visionRadius; j++) {
                    var yNew = this.robot.me.y + i;
                    var xNew = this.robot.me.x + j;
                    var tile = new bc19.Position(yNew, xNew);
                    if (bc19.Helper.inMap(this.robot.map, tile)) {
                        if (bc19.Helper.DistanceSquared(tile, this.location) > this.robot.SPECS.UNITS[this.robot.SPECS.PILGRIM].VISION_RADIUS) {
                            continue;
                        }
                        if (!this.robot.getKarboniteMap()[yNew][xNew] && !this.robot.getFuelMap()[yNew][xNew]) {
                            this.occupiedResources[yNew][xNew] = -1;
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
                }
                ;
            }
            ;
        };
        Pilgrim.prototype.GetNearestDropOff = function () {
            var lowest = Number.MAX_VALUE;
            var closest = null;
            for (var i = 0; i < this.dropOffLocations.length; i++) {
                var pos = this.dropOffLocations[i];
                var distance = (function (m, k) { if (m.entries == null)
                    m.entries = []; for (var i_5 = 0; i_5 < m.entries.length; i_5++)
                    if (m.entries[i_5].key.equals != null && m.entries[i_5].key.equals(k) || m.entries[i_5].key === k) {
                        return true;
                    } return false; })(this.ourDropOffRoutes, pos) ? (function (m, k) { if (m.entries == null)
                    m.entries = []; for (var i_6 = 0; i_6 < m.entries.length; i_6++)
                    if (m.entries[i_6].key.equals != null && m.entries[i_6].key.equals(k) || m.entries[i_6].key === k) {
                        return m.entries[i_6].value;
                    } return null; })(this.ourDropOffRoutes, pos)[this.location.y][this.location.x] : bc19.Helper.DistanceSquared(pos, this.location);
                if (this.occupiedResources[pos.y][pos.x] !== 1 && distance < lowest) {
                    lowest = distance;
                    closest = pos;
                }
            }
            ;
            return closest;
        };
        Pilgrim.prototype.ReturnToDropOff = function () {
            var dropOff = this.GetNearestDropOff();
            if (bc19.Helper.DistanceSquared(dropOff, this.location) < 3) {
                this.WhatToMine();
                var throwAway = ([]);
                var signals = this.ReadInitialSignals(this.robot, throwAway);
                this.maxKarb = signals[2] ? this.emergencyAmount : this.robot.SPECS.UNITS[this.robot.me.unit].KARBONITE_CAPACITY;
                this.state = bc19.PilgrimState.GoingToResource;
                return this.robot.give(dropOff.x - this.location.x, dropOff.y - this.location.y, this.robot.me.karbonite, this.robot.me.fuel);
            }
            else if (bc19.Helper.DistanceSquared(dropOff, this.location) <= this.robot.SPECS.UNITS[this.robot.me.unit].SPEED) {
                var nextToDropoff = bc19.Helper.RandomNonResourceAdjacentPositionInMoveRange(this.robot, dropOff);
                if (nextToDropoff != null) {
                    return this.robot.move(nextToDropoff.x - this.location.x, nextToDropoff.y - this.location.y);
                }
                return this.MoveCloser(this.robot, dropOff);
            }
            else {
                return this.FloodPathing(this.robot, this.GetOrCreateMap(this.robot, this.karbRoutes, dropOff), dropOff);
            }
        };
        Pilgrim.prototype.GetNearestResource = function () {
            var chosenPositions = this.miningKarb ? this.karbLocations : this.fuelLocations;
            var chosenMaps = this.miningKarb ? this.karbRoutes : this.fuelRoutes;
            var lowest = Number.MAX_VALUE;
            var closest = null;
            for (var i = 0; i < chosenPositions.length; i++) {
                var pos = chosenPositions[i];
                var distance = (function (m, k) { if (m.entries == null)
                    m.entries = []; for (var i_7 = 0; i_7 < m.entries.length; i_7++)
                    if (m.entries[i_7].key.equals != null && m.entries[i_7].key.equals(k) || m.entries[i_7].key === k) {
                        return true;
                    } return false; })(chosenMaps, pos) ? (function (m, k) { if (m.entries == null)
                    m.entries = []; for (var i_8 = 0; i_8 < m.entries.length; i_8++)
                    if (m.entries[i_8].key.equals != null && m.entries[i_8].key.equals(k) || m.entries[i_8].key === k) {
                        return m.entries[i_8].value;
                    } return null; })(chosenMaps, pos)[this.location.y][this.location.x] : bc19.Helper.DistanceSquared(pos, this.location);
                if ((this.occupiedResources[pos.y][pos.x] !== 1 || (this.location.y === pos.y && this.location.x === pos.x)) && distance < lowest) {
                    lowest = distance;
                    closest = pos;
                }
            }
            ;
            return closest;
        };
        Pilgrim.prototype.GoToMine = function () {
            var nearest = this.GetNearestResource();
            var movespeed = this.robot.SPECS.UNITS[this.robot.me.unit].SPEED;
            if (nearest.y - this.location.y === 0 && nearest.x - this.location.x === 0) {
                this.state = bc19.PilgrimState.Mining;
                return this.robot.mine();
            }
            else if (bc19.Helper.DistanceSquared(nearest, this.location) <= movespeed) {
                if (this.occupiedResources[nearest.y][nearest.x] === 2) {
                    this.waitCounter++;
                    if (this.waitCounter >= this.waitMax) {
                        this.occupiedResources[nearest.y][nearest.x] = 1;
                        this.GoToMine();
                    }
                    return null;
                }
                else {
                    this.state = bc19.PilgrimState.Mining;
                    return this.robot.move(nearest.x - this.location.x, nearest.y - this.location.y);
                }
            }
            else {
                this.state = bc19.PilgrimState.GoingToResource;
                if (this.miningKarb === true) {
                    return this.FloodPathing(this.robot, this.GetOrCreateMap(this.robot, this.karbRoutes, nearest), nearest);
                }
                else {
                    return this.FloodPathing(this.robot, this.GetOrCreateMap(this.robot, this.fuelRoutes, nearest), nearest);
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
            else if (this.robot.fuel > this.robot.karbonite * 8) {
                this.miningKarb = true;
            }
            else {
                this.miningKarb = false;
            }
        };
        Pilgrim.prototype.Mining = function () {
            if (this.occupiedResources[this.location.y][this.location.x] === -1 || (!this.robot.getKarboniteMap()[this.location.y][this.location.x] && !this.robot.getFuelMap()[this.location.y][this.location.x])) {
                this.state = bc19.PilgrimState.GoingToResource;
            }
            if (this.robot.me.karbonite >= this.maxKarb || this.robot.me.fuel >= this.maxFuel) {
                this.state = bc19.PilgrimState.Returning;
                var church = this.BuildChurch();
                return church == null ? this.ReturnToDropOff() : church;
            }
            else {
                return this.robot.mine();
            }
        };
        Pilgrim.prototype.ShouldBuildChurch = function () {
            this.CheckForChurch();
            var dropOff = this.GetNearestDropOff();
            if (this.maxKarb === this.emergencyAmount) {
                return false;
            }
            var cost = this.FuelToReturn(this.GetOrCreateMap(this.robot, this.ourDropOffRoutes, dropOff));
            if (cost > this.oportunityKarbLostThreshold) {
                return true;
            }
            else {
                return false;
            }
        };
        Pilgrim.prototype.BuildChurch = function () {
            if (this.ShouldBuildChurch()) {
                if (bc19.Helper.CanAfford(this.robot, this.robot.SPECS.CHURCH)) {
                    var buildChurchHere = bc19.Helper.RandomNonResourceAdjacentPosition(this.robot, this.location);
                    var dx = buildChurchHere.x - this.location.x;
                    var dy = buildChurchHere.y - this.location.y;
                    this.state = bc19.PilgrimState.Returning;
                    /* add */ (this.dropOffLocations.push(buildChurchHere) > 0);
                    return this.robot.buildUnit(this.robot.SPECS.CHURCH, dx, dy);
                }
            }
            return null;
        };
        Pilgrim.prototype.FuelToReturn = function (path) {
            var valueUnderTile = path[this.location.y][this.location.x];
            var tileMovementSpeed = Math.fround(Math.sqrt(this.robot.SPECS.UNITS[this.robot.SPECS.PILGRIM].SPEED));
            var amountOfMoves = Math.fround(valueUnderTile / tileMovementSpeed);
            var cost = Math.fround(amountOfMoves * this.robot.SPECS.KARBONITE_YIELD);
            return cost;
        };
        Pilgrim.prototype.CheckForChurch = function () {
            var robots = this.robot.getVisibleRobots();
            for (var i = 0; i < robots.length; i++) {
                this.ourTeam = this.robot.me.team === this.robot.SPECS.RED ? 0 : 1;
                if (robots[i].unit === this.robot.SPECS.CHURCH && robots[i].team === this.ourTeam) {
                    if (this.dropOffLocations == null) {
                        this.dropOffLocations = ([]);
                        /* add */ (this.dropOffLocations.push(new bc19.Position(robots[i].y, robots[i].x)) > 0);
                        return true;
                    }
                    for (var j = 0; j < this.dropOffLocations.length; j++) {
                        if (robots[i].y !== this.dropOffLocations[j].y || robots[i].x !== this.dropOffLocations[j].x) {
                            /* add */ (this.dropOffLocations.push(new bc19.Position(robots[i].y, robots[i].x)) > 0);
                            return true;
                        }
                    }
                    ;
                }
            }
            ;
            return false;
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
    })(PilgrimState = bc19.PilgrimState || (bc19.PilgrimState = {}));
})(bc19 || (bc19 = {}));
(function (bc19) {
    var Preacher = (function (_super) {
        __extends(Preacher, _super);
        function Preacher(robot) {
            var _this = _super.call(this) || this;
            _this.doneUpgrading = false;
            _this.robot = null;
            _this.ourTeam = 0;
            _this.location = null;
            _this.initialized = false;
            _this.mapIsHorizontal = false;
            _this.castleLocations = null;
            _this.enemyCastleLocations = null;
            _this.routesToEnemies = null;
            _this.closestCastle = null;
            _this.state = null;
            _this.previousHealth = 0;
            _this.toBeUpgraded = null;
            _this.robot = robot;
            return _this;
        }
        Preacher.prototype.Execute = function () {
            this.location = new bc19.Position(this.robot.me.y, this.robot.me.x);
            if (this.robot.me.turn === 1) {
                this.InitializeVariables();
            }
            if (this.EnemiesAround(this.robot, this.ourTeam)) {
                return this.AttackEnemies();
            }
            if (!this.initialized) {
                this.Initialize();
            }
            if (!this.doneUpgrading) {
                this.doneUpgrading = this.UpgradeMaps(this.robot, this.routesToEnemies, this.toBeUpgraded);
            }
            this.UnderSiege();
            if (this.state === bc19.PreacherState.UnderSiege) {
                this.state = bc19.PreacherState.Mobilizing;
            }
            if (this.state === bc19.PreacherState.Fortifying || this.state === bc19.PreacherState.MovingToDefencePosition) {
                if (this.WatchForSignal(this.robot, 65535)) {
                    this.state = bc19.PreacherState.Mobilizing;
                }
                if (this.state === bc19.PreacherState.MovingToDefencePosition) {
                    if (bc19.Helper.IsSurroundingsOccupied(this.robot, this.robot.getVisibleRobotMap(), this.location, this.ourTeam) < 2) {
                        this.state = bc19.PreacherState.Fortifying;
                    }
                    else {
                        this.GetClosestCastle();
                        return this.MoveToDefend();
                    }
                }
            }
            else if (this.state === bc19.PreacherState.Mobilizing) {
                var closestEnemyCastle_1 = this.ClosestEnemyCastle(this.robot, this.routesToEnemies);
                var robots = this.robot.getVisibleRobots();
                if (bc19.Helper.DistanceSquared(new bc19.Position(this.robot.me.y, this.robot.me.x), closestEnemyCastle_1) <= this.robot.SPECS.UNITS[this.robot.me.unit].VISION_RADIUS) {
                    var absent = true;
                    for (var j = 0; j < robots.length; j++) {
                        if (robots[j].y === closestEnemyCastle_1.y && robots[j].x === closestEnemyCastle_1.x && robots[j].unit === this.robot.SPECS.CASTLE) {
                            absent = false;
                        }
                    }
                    ;
                    if (absent) {
                        /* remove */ (function (a) { return a.splice(a.indexOf(closestEnemyCastle_1), 1); })(this.enemyCastleLocations);
                        closestEnemyCastle_1 = this.ClosestEnemyCastle(this.robot, this.routesToEnemies);
                    }
                }
                return this.CombatFloodPathing(this.robot, this.GetOrCreateMap(this.robot, this.routesToEnemies, closestEnemyCastle_1), closestEnemyCastle_1, this.ourTeam);
            }
            return null;
        };
        Preacher.prototype.Initialize = function () {
            if (this.robot.me.turn === 1) {
                this.state = bc19.PreacherState.Initializing;
            }
            if (!this.initialized) {
                var signals = this.ReadInitialSignals(this.robot, this.castleLocations);
                this.initialized = signals[0];
                if (this.initialized) {
                    this.enemyCastleLocations = bc19.Helper.FindEnemyCastles(this.robot, this.mapIsHorizontal, this.castleLocations);
                    this.toBeUpgraded = (this.enemyCastleLocations.slice(0));
                    for (var i = 0; i < this.enemyCastleLocations.length; i++) {
                        this.GetOrCreateMap(this.robot, this.routesToEnemies, /* get */ this.enemyCastleLocations[i]);
                    }
                    ;
                }
                if (this.initialized && signals[1]) {
                    this.state = bc19.PreacherState.Mobilizing;
                }
                else if (this.initialized) {
                    this.state = bc19.PreacherState.MovingToDefencePosition;
                }
            }
        };
        Preacher.prototype.InitializeVariables = function () {
            this.ourTeam = this.robot.me.team === this.robot.SPECS.RED ? 0 : 1;
            this.mapIsHorizontal = bc19.Helper.FindSymmetry(this.robot.map);
            this.location = new bc19.Position(this.robot.me.y, this.robot.me.x);
            this.castleLocations = ([]);
            this.enemyCastleLocations = ([]);
            this.routesToEnemies = ({});
            this.initialized = false;
            this.previousHealth = this.robot.SPECS.UNITS[this.robot.me.unit].STARTING_HP;
            this.GetClosestCastle();
        };
        Preacher.prototype.UnderSiege = function () {
            if (this.previousHealth !== this.robot.me.health && (!this.EnemiesAround(this.robot, this.ourTeam) || this.WatchForSignal(this.robot, 0))) {
                this.robot.signal(0, 9);
                this.state = bc19.PreacherState.UnderSiege;
            }
            this.previousHealth = this.robot.me.health;
        };
        Preacher.prototype.AttackEnemies = function () {
            var most = Number.MIN_VALUE;
            var attackTile = null;
            var visionRange = this.robot.SPECS.UNITS[this.robot.me.unit].VISION_RADIUS;
            var visionRadius = (Math.sqrt(visionRange) | 0);
            for (var i = -visionRadius; i <= visionRadius; i++) {
                for (var j = -visionRadius; j <= visionRadius; j++) {
                    var checkTile = new bc19.Position(this.location.y + i, this.location.x + j);
                    if (bc19.Helper.inMap(this.robot.map, checkTile) && bc19.Helper.DistanceSquared(checkTile, this.location) <= visionRange) {
                        var mostEnemies = this.NumAdjacentEnemies(checkTile);
                        if (mostEnemies > most) {
                            most = mostEnemies;
                            attackTile = checkTile;
                        }
                    }
                    else {
                        continue;
                    }
                }
                ;
            }
            ;
            return this.robot.attack(attackTile.x - this.location.x, attackTile.y - this.location.y);
        };
        Preacher.prototype.NumAdjacentEnemies = function (pos) {
            var numEnemies = 0;
            var robots = this.robot.getVisibleRobots();
            for (var i = -1; i <= 1; i++) {
                for (var j = -1; j <= 1; j++) {
                    for (var k = 0; k < robots.length; k++) {
                        if (pos.y + i === robots[k].y && pos.x + j === robots[k].x) {
                            if (robots[k].team === this.ourTeam && robots[k].unit === this.robot.SPECS.CASTLE) {
                                numEnemies -= 8;
                            }
                            else if (robots[k].team === this.ourTeam) {
                                numEnemies--;
                            }
                            else {
                                numEnemies++;
                            }
                        }
                        else {
                            continue;
                        }
                    }
                    ;
                }
                ;
            }
            ;
            return numEnemies;
        };
        Preacher.prototype.MoveToDefend = function () {
            var robotPos = new bc19.Position(this.robot.me.y, this.robot.me.x);
            var enemyCastle = bc19.Helper.FindEnemyCastle(this.robot.map, this.mapIsHorizontal, this.closestCastle);
            var distFromCastleToCastle = bc19.Helper.DistanceSquared(this.closestCastle, enemyCastle);
            var movespeed = this.robot.SPECS.UNITS[this.robot.me.unit].SPEED;
            var moveRange = (Math.sqrt(this.robot.SPECS.UNITS[this.robot.me.unit].SPEED) | 0);
            for (var i = -moveRange; i <= moveRange; i++) {
                for (var j = -moveRange; j <= moveRange; j++) {
                    var defenceTile = new bc19.Position(this.robot.me.y + i, this.robot.me.x + j);
                    var distFromTileToEnemyCastle = bc19.Helper.DistanceSquared(defenceTile, enemyCastle);
                    if (bc19.Helper.inMap(this.robot.map, defenceTile) && this.robot.map[defenceTile.y][defenceTile.x] && distFromTileToEnemyCastle < distFromCastleToCastle) {
                        if (bc19.Helper.IsSurroundingsOccupied(this.robot, this.robot.getVisibleRobotMap(), defenceTile, this.ourTeam) < 2) {
                            var moveDistance = bc19.Helper.DistanceSquared(defenceTile, robotPos);
                            if (moveDistance <= movespeed) {
                                return this.robot.move(defenceTile.x - this.robot.me.x, defenceTile.y - this.robot.me.y);
                            }
                            else {
                                return this.MoveCloser(this.robot, defenceTile);
                            }
                        }
                    }
                }
                ;
            }
            ;
            return this.MoveCloser(this.robot, enemyCastle);
        };
        Preacher.prototype.GetClosestCastle = function () {
            var least = Number.MAX_VALUE;
            for (var index139 = 0; index139 < this.castleLocations.length; index139++) {
                var castlePos = this.castleLocations[index139];
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
    var PreacherState;
    (function (PreacherState) {
        PreacherState[PreacherState["Initializing"] = 0] = "Initializing";
        PreacherState[PreacherState["Fortifying"] = 1] = "Fortifying";
        PreacherState[PreacherState["MovingToDefencePosition"] = 2] = "MovingToDefencePosition";
        PreacherState[PreacherState["UnderSiege"] = 3] = "UnderSiege";
        PreacherState[PreacherState["Mobilizing"] = 4] = "Mobilizing";
    })(PreacherState = bc19.PreacherState || (bc19.PreacherState = {}));
})(bc19 || (bc19 = {}));
(function (bc19) {
    var Prophet = (function (_super) {
        __extends(Prophet, _super);
        function Prophet(robot) {
            var _this = _super.call(this) || this;
            _this.doneUpgrading = false;
            _this.robot = null;
            _this.ourTeam = 0;
            _this.location = null;
            _this.initialized = false;
            _this.mapIsHorizontal = false;
            _this.closestCastle = null;
            _this.castleLocations = null;
            _this.enemyCastleLocations = null;
            _this.routesToEnemies = null;
            _this.previousHealth = 0;
            _this.state = null;
            _this.toBeUpgraded = null;
            _this.robot = robot;
            return _this;
        }
        Prophet.prototype.Execute = function () {
            this.location = new bc19.Position(this.robot.me.y, this.robot.me.x);
            if (this.robot.me.turn === 1) {
                this.InitializeVariables();
            }
            if (this.EnemiesAround(this.robot, this.ourTeam)) {
                return this.AttackEnemies();
            }
            if (!this.initialized) {
                this.Initialize();
            }
            if (this.initialized) {
                if (!this.doneUpgrading) {
                    this.doneUpgrading = this.UpgradeMaps(this.robot, this.routesToEnemies, this.toBeUpgraded);
                }
                if (this.state === bc19.ProphetState.Fortifying || this.state === bc19.ProphetState.MovingToDefencePosition) {
                    this.state = bc19.ProphetState.Mobilizing;
                }
                else if (this.state === bc19.ProphetState.Mobilizing) {
                    var closestEnemyCastle_2 = this.ClosestEnemyCastle(this.robot, this.routesToEnemies);
                    var robots = this.robot.getVisibleRobots();
                    if (bc19.Helper.DistanceSquared(new bc19.Position(this.robot.me.y, this.robot.me.x), closestEnemyCastle_2) <= this.robot.SPECS.UNITS[this.robot.me.unit].VISION_RADIUS) {
                        var absent = true;
                        for (var j = 0; j < robots.length; j++) {
                            if (robots[j].y === closestEnemyCastle_2.y && robots[j].x === closestEnemyCastle_2.x && robots[j].unit === this.robot.SPECS.CASTLE) {
                                absent = false;
                            }
                        }
                        ;
                        if (absent) {
                            /* remove */ (function (a) { return a.splice(a.indexOf(closestEnemyCastle_2), 1); })(this.enemyCastleLocations);
                            closestEnemyCastle_2 = this.ClosestEnemyCastle(this.robot, this.routesToEnemies);
                        }
                    }
                    return this.CombatFloodPathing(this.robot, this.GetOrCreateMap(this.robot, this.routesToEnemies, closestEnemyCastle_2), closestEnemyCastle_2, this.ourTeam);
                }
            }
            return null;
        };
        Prophet.prototype.Initialize = function () {
            if (this.robot.me.turn === 1) {
                this.state = bc19.ProphetState.Initializing;
            }
            if (!this.initialized) {
                var signals = this.ReadInitialSignals(this.robot, this.castleLocations);
                this.initialized = signals[0];
                if (this.initialized) {
                    this.enemyCastleLocations = bc19.Helper.FindEnemyCastles(this.robot, this.mapIsHorizontal, this.castleLocations);
                    this.toBeUpgraded = (this.enemyCastleLocations.slice(0));
                    for (var i = 0; i < this.enemyCastleLocations.length; i++) {
                        this.GetOrCreateMap(this.robot, this.routesToEnemies, /* get */ this.enemyCastleLocations[i]);
                    }
                    ;
                }
                if (this.initialized && signals[1]) {
                    this.state = bc19.ProphetState.Mobilizing;
                }
                else if (this.initialized) {
                    this.state = bc19.ProphetState.MovingToDefencePosition;
                }
            }
        };
        Prophet.prototype.InitializeVariables = function () {
            this.ourTeam = this.robot.me.team === this.robot.SPECS.RED ? 0 : 1;
            this.mapIsHorizontal = bc19.Helper.FindSymmetry(this.robot.map);
            this.location = new bc19.Position(this.robot.me.y, this.robot.me.x);
            this.castleLocations = ([]);
            this.enemyCastleLocations = ([]);
            this.routesToEnemies = ({});
            this.initialized = false;
            this.previousHealth = this.robot.SPECS.UNITS[this.robot.me.unit].STARTING_HP;
            this.GetClosestCastle();
        };
        Prophet.prototype.AttackEnemies = function () {
            var robots = this.robot.getVisibleRobots();
            var attackTile = null;
            var lowestID = Number.MAX_VALUE;
            for (var i = 0; i < robots.length; i++) {
                var visibleRobot = new bc19.Position(robots[i].y, robots[i].x);
                var withinRange = bc19.Helper.DistanceSquared(visibleRobot, this.location);
                if (robots[i].team !== this.ourTeam && withinRange <= this.robot.SPECS.UNITS[this.robot.me.unit].VISION_RADIUS) {
                    var robotID = robots[i].id;
                    if (robotID < lowestID) {
                        lowestID = robotID;
                        attackTile = visibleRobot;
                    }
                }
            }
            ;
            return this.robot.attack(attackTile.x - this.location.x, attackTile.y - this.location.y);
        };
        Prophet.prototype.MoveToDefend = function () {
            var robotPos = new bc19.Position(this.robot.me.y, this.robot.me.x);
            var enemyCastle = bc19.Helper.FindEnemyCastle(this.robot.map, this.mapIsHorizontal, this.closestCastle);
            var movespeed = this.robot.SPECS.UNITS[this.robot.me.unit].SPEED;
            var moveRange = (Math.sqrt(this.robot.SPECS.UNITS[this.robot.me.unit].SPEED) | 0);
            for (var i = -moveRange; i <= moveRange; i++) {
                for (var j = -moveRange; j <= moveRange; j++) {
                    var defenceTile = new bc19.Position(this.robot.me.y + i, this.robot.me.x + j);
                    if (bc19.Helper.inMap(this.robot.map, defenceTile) && this.robot.map[defenceTile.y][defenceTile.x]) {
                        if (bc19.Helper.IsSurroundingsOccupied(this.robot, this.robot.getVisibleRobotMap(), defenceTile, this.ourTeam) < 2) {
                            var moveDistance = bc19.Helper.DistanceSquared(defenceTile, robotPos);
                            if (moveDistance <= movespeed) {
                                return this.robot.move(defenceTile.x - this.robot.me.x, defenceTile.y - this.robot.me.y);
                            }
                            else {
                                return this.MoveCloser(this.robot, defenceTile);
                            }
                        }
                    }
                }
                ;
            }
            ;
            return this.MoveCloser(this.robot, enemyCastle);
        };
        Prophet.prototype.GetClosestCastle = function () {
            var least = Number.MAX_VALUE;
            for (var index140 = 0; index140 < this.castleLocations.length; index140++) {
                var castlePos = this.castleLocations[index140];
                {
                    var distance = bc19.Helper.DistanceSquared(castlePos, this.location);
                    if (distance < least) {
                        least = distance;
                        this.closestCastle = castlePos;
                    }
                }
            }
        };
        return Prophet;
    }(bc19.MovingRobot));
    bc19.Prophet = Prophet;
    Prophet["__class"] = "bc19.Prophet";
    Prophet["__interfaces"] = ["bc19.Machine"];
    var ProphetState;
    (function (ProphetState) {
        ProphetState[ProphetState["Initializing"] = 0] = "Initializing";
        ProphetState[ProphetState["Fortifying"] = 1] = "Fortifying";
        ProphetState[ProphetState["MovingToDefencePosition"] = 2] = "MovingToDefencePosition";
        ProphetState[ProphetState["Mobilizing"] = 3] = "Mobilizing";
    })(ProphetState = bc19.ProphetState || (bc19.ProphetState = {}));
})(bc19 || (bc19 = {}));
(function (bc19) {
    var MyRobot = (function (_super) {
        __extends(MyRobot, _super);
        function MyRobot() {
            var _this = _super.call(this) || this;
            _this.debugTurn = 0;
            _this.robot = null;
            _this.test = null;
            return _this;
        }
        MyRobot.prototype.turn = function () {
            this.debugTurn++;
            if (this.robot == null) {
                if (this.me.unit === this.SPECS.CASTLE) {
                    this.log("I am a Castle");
                    this.robot = new bc19.Castle(this);
                }
                else if (this.me.unit === this.SPECS.CHURCH) {
                    this.log("I am a ChuUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUrch");
                    this.robot = new bc19.Church(this);
                }
                else if (this.me.unit === this.SPECS.PILGRIM) {
                    this.log("I am a Pilgrim");
                    this.robot = new bc19.Pilgrim(this);
                }
                else if (this.me.unit === this.SPECS.CRUSADER) {
                    this.robot = new bc19.Crusader(this);
                }
                else if (this.me.unit === this.SPECS.PROPHET) {
                    this.log("I am a Prophet");
                    this.robot = new bc19.Prophet(this);
                }
                else if (this.me.unit === this.SPECS.PREACHER) {
                    this.log("I am a Preacher");
                    this.robot = new bc19.Preacher(this);
                }
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
            return "(" + ('' + (this.y)) + ", " + ('' + (this.x)) + ")";
        };
        return Position;
    }());
    bc19.Position = Position;
    Position["__class"] = "bc19.Position";
})(bc19 || (bc19 = {}));
//# sourceMappingURL=bundle.js.map
var specs = {"COMMUNICATION_BITS":16,"CASTLE_TALK_BITS":8,"MAX_ROUNDS":1000,"TRICKLE_FUEL":25,"INITIAL_KARBONITE":100,"INITIAL_FUEL":500,"MINE_FUEL_COST":1,"KARBONITE_YIELD":2,"FUEL_YIELD":10,"MAX_TRADE":1024,"MAX_BOARD_SIZE":64,"MAX_ID":4096,"CASTLE":0,"CHURCH":1,"PILGRIM":2,"CRUSADER":3,"PROPHET":4,"PREACHER":5,"RED":0,"BLUE":1,"CHESS_INITIAL":100,"CHESS_EXTRA":20,"TURN_MAX_TIME":200,"MAX_MEMORY":50000000,"UNITS":[{"CONSTRUCTION_KARBONITE":null,"CONSTRUCTION_FUEL":null,"KARBONITE_CAPACITY":null,"FUEL_CAPACITY":null,"SPEED":0,"FUEL_PER_MOVE":null,"STARTING_HP":100,"VISION_RADIUS":100,"ATTACK_DAMAGE":null,"ATTACK_RADIUS":null,"ATTACK_FUEL_COST":null,"DAMAGE_SPREAD":null},{"CONSTRUCTION_KARBONITE":50,"CONSTRUCTION_FUEL":200,"KARBONITE_CAPACITY":null,"FUEL_CAPACITY":null,"SPEED":0,"FUEL_PER_MOVE":null,"STARTING_HP":50,"VISION_RADIUS":100,"ATTACK_DAMAGE":null,"ATTACK_RADIUS":null,"ATTACK_FUEL_COST":null,"DAMAGE_SPREAD":null},{"CONSTRUCTION_KARBONITE":10,"CONSTRUCTION_FUEL":50,"KARBONITE_CAPACITY":20,"FUEL_CAPACITY":100,"SPEED":4,"FUEL_PER_MOVE":1,"STARTING_HP":10,"VISION_RADIUS":100,"ATTACK_DAMAGE":null,"ATTACK_RADIUS":null,"ATTACK_FUEL_COST":null,"DAMAGE_SPREAD":null},{"CONSTRUCTION_KARBONITE":20,"CONSTRUCTION_FUEL":50,"KARBONITE_CAPACITY":20,"FUEL_CAPACITY":100,"SPEED":9,"FUEL_PER_MOVE":1,"STARTING_HP":40,"VISION_RADIUS":36,"ATTACK_DAMAGE":10,"ATTACK_RADIUS":[1,16],"ATTACK_FUEL_COST":10,"DAMAGE_SPREAD":0},{"CONSTRUCTION_KARBONITE":25,"CONSTRUCTION_FUEL":50,"KARBONITE_CAPACITY":20,"FUEL_CAPACITY":100,"SPEED":4,"FUEL_PER_MOVE":2,"STARTING_HP":20,"VISION_RADIUS":64,"ATTACK_DAMAGE":10,"ATTACK_RADIUS":[16,64],"ATTACK_FUEL_COST":25,"DAMAGE_SPREAD":0},{"CONSTRUCTION_KARBONITE":30,"CONSTRUCTION_FUEL":50,"KARBONITE_CAPACITY":20,"FUEL_CAPACITY":100,"SPEED":4,"FUEL_PER_MOVE":3,"STARTING_HP":60,"VISION_RADIUS":16,"ATTACK_DAMAGE":20,"ATTACK_RADIUS":[1,16],"ATTACK_FUEL_COST":15,"DAMAGE_SPREAD":3}]};
var robot = new bc19.MyRobot(); robot.setSpecs(specs);