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
        MovingRobot.CreateLayeredFloodPath$bc19_MyRobot$bc19_Position = function (robot, startPos) {
            return MovingRobot.CreateLayeredFloodPath$bc19_MyRobot$bc19_Position$bc19_Position(robot, startPos, new bc19.Position(1000, 1000));
        };
        MovingRobot.CreateLayeredFloodPath$bc19_MyRobot$bc19_Position$bc19_Position = function (robot, startPos, endPos) {
            var singleStep = (function (dims) { var allocate = function (dims) { if (dims.length == 0) {
                return 0;
            }
            else {
                var array = [];
                for (var i = 0; i < dims[0]; i++) {
                    array.push(allocate(dims.slice(1)));
                }
                return array;
            } }; return allocate(dims); })([robot.map.length, robot.map[0].length]);
            var toBeVisited = ([]);
            /* add */ (toBeVisited.push(new bc19.PathingPosition(startPos, 0)) > 0);
            var robots = robot.getVisibleRobots();
            for (var i = 0; i < robots.length; i++) {
                if (bc19.Helper.DistanceSquared(robot.location, new bc19.Position(robots[i].y, robots[i].x)) <= robot.visionRange) {
                    if (robots[i].unit === robot.SPECS.CASTLE || robots[i].unit === robot.SPECS.CHURCH) {
                        singleStep[robots[i].y][robots[i].x] = -1;
                    }
                }
            }
            ;
            while ((toBeVisited.length > 0)) {
                var removed = (function (a) { return a.length == 0 ? null : a.shift(); })(toBeVisited);
                for (var y = -robot.tileMovementRange; y <= robot.tileMovementRange; y++) {
                    for (var x = -robot.tileMovementRange; x <= robot.tileMovementRange; x++) {
                        var relativePosition = new bc19.Position(removed.pos.y + y, removed.pos.x + x);
                        if (bc19.Helper.inMap(robot.map, relativePosition) && bc19.Helper.DistanceSquared(relativePosition, removed.pos) <= robot.movementRange) {
                            if (relativePosition.y === endPos.y && relativePosition.x === endPos.x) {
                                return singleStep;
                            }
                            if (x === 0 && y === 0) {
                                continue;
                            }
                            if (!robot.map[relativePosition.y][relativePosition.x]) {
                                singleStep[relativePosition.y][relativePosition.x] = -1;
                                continue;
                            }
                            var newCumulitive = Math.fround(Math.fround(removed.cumulative + x * x) + y * y);
                            if (singleStep[relativePosition.y][relativePosition.x] > newCumulitive) {
                                singleStep[relativePosition.y][relativePosition.x] = newCumulitive;
                                continue;
                            }
                            if (singleStep[relativePosition.y][relativePosition.x] !== 0) {
                                continue;
                            }
                            var decrementedRange = robot.tileMovementRange === 2 ? 2 : 6;
                            singleStep[relativePosition.y][relativePosition.x] = newCumulitive;
                            if (bc19.Helper.DistanceSquared(relativePosition, removed.pos) >= decrementedRange) {
                                /* add */ (toBeVisited.push(new bc19.PathingPosition(relativePosition, newCumulitive)) > 0);
                            }
                        }
                    }
                    ;
                }
                ;
            }
            ;
            return singleStep;
        };
        MovingRobot.CreateLayeredFloodPath = function (robot, startPos, endPos) {
            if (((robot != null && robot instanceof bc19.MyRobot) || robot === null) && ((startPos != null && startPos instanceof bc19.Position) || startPos === null) && ((endPos != null && endPos instanceof bc19.Position) || endPos === null)) {
                return bc19.MovingRobot.CreateLayeredFloodPath$bc19_MyRobot$bc19_Position$bc19_Position(robot, startPos, endPos);
            }
            else if (((robot != null && robot instanceof bc19.MyRobot) || robot === null) && ((startPos != null && startPos instanceof bc19.Position) || startPos === null) && endPos === undefined) {
                return bc19.MovingRobot.CreateLayeredFloodPath$bc19_MyRobot$bc19_Position(robot, startPos);
            }
            else
                throw new Error('invalid overload');
        };
        MovingRobot.FloodPathing = function (robot, path, goal, budget) {
            if (path == null) {
                return null;
            }
            var tileMoveRange = budget ? 1 : robot.tileMovementRange;
            var moveRange = budget ? 3 : robot.movementRange;
            if (bc19.Helper.DistanceSquared(robot.location, goal) <= robot.movementRange) {
                if (robot.location.equals(goal)) {
                    return null;
                }
                if (bc19.Helper.TileEmpty(robot, goal)) {
                    return robot.move(goal.x - robot.me.x, goal.y - robot.me.y);
                }
                else if (bc19.Helper.DistanceSquared(robot.location, goal) > 3) {
                    var adj = bc19.Helper.RandomAdjacentMoveable(robot, goal, robot.movementRange);
                    if (adj != null) {
                        return robot.move(adj.x - robot.me.x, adj.y - robot.me.y);
                    }
                    else {
                        return null;
                    }
                }
            }
            else {
                var lowestPos = MovingRobot.LowestOnPathInMoveRange(robot, path, goal, tileMoveRange, moveRange);
                if (!lowestPos.equals(robot.location)) {
                    return robot.move(lowestPos.x - robot.me.x, lowestPos.y - robot.me.y);
                }
                lowestPos = MovingRobot.LowestOnPathInMoveRange(robot, path, goal, robot.tileMovementRange, robot.movementRange);
                if (!lowestPos.equals(robot.location)) {
                    return robot.move(lowestPos.x - robot.me.x, lowestPos.y - robot.me.y);
                }
                return null;
            }
            return null;
        };
        MovingRobot.LowestOnPathInMoveRange = function (robot, path, goal, tileMoveRange, moveRange) {
            var validPositions = bc19.Helper.AllOpenInRange(robot, robot.location, tileMoveRange, moveRange);
            var lowest = path[robot.me.y][robot.me.x] === 0 ? Number.MAX_VALUE : Math.fround(path[robot.me.y][robot.me.x] + 1);
            var lowestPos = robot.location;
            for (var i = 0; i < validPositions.length; i++) {
                var possible = validPositions[i];
                if (path[possible.y][possible.x] > 0 && !possible.equals(robot.location) && !possible.equals(robot.previousLocation)) {
                    if ((possible.x - robot.me.x) === 0 || (possible.y - robot.me.y) === 0) {
                        if (path[possible.y][possible.x] < lowest || (path[possible.y][possible.x] === lowest && (bc19.Helper.DistanceSquared(possible, goal) < bc19.Helper.DistanceSquared(lowestPos, goal)))) {
                            lowest = path[possible.y][possible.x];
                            lowestPos = possible;
                        }
                    }
                    else {
                        if (path[possible.y][possible.x] < Math.fround(lowest - 1) || (path[possible.y][possible.x] === Math.fround(lowest - 1) && (bc19.Helper.DistanceSquared(possible, goal) < bc19.Helper.DistanceSquared(lowestPos, goal)))) {
                            lowest = path[possible.y][possible.x];
                            lowestPos = possible;
                        }
                    }
                }
            }
            ;
            return lowestPos;
        };
        MovingRobot.MoveCloser = function (robot, pos, budget) {
            var output = null;
            if (budget) {
                output = MovingRobot.LowestInRange(robot, pos, 1);
            }
            if (output == null) {
                output = MovingRobot.LowestInRange(robot, pos, robot.tileMovementRange);
            }
            if (output != null) {
                return robot.move(output.x - robot.me.x, output.y - robot.me.y);
            }
            return null;
        };
        MovingRobot.LowestInRange = function (robot, pos, tileRange) {
            var closest = Number.MAX_VALUE;
            var output = null;
            if (bc19.Helper.TileEmpty(robot, pos) && bc19.Helper.DistanceSquared(pos, robot.location) <= robot.movementRange) {
                return pos;
            }
            for (var y = -tileRange; y <= tileRange; y++) {
                for (var x = -tileRange; x <= tileRange; x++) {
                    var possible = new bc19.Position(robot.me.y + y, robot.me.x + x);
                    if (bc19.Helper.TileEmpty(robot, possible) && bc19.Helper.DistanceSquared(robot.location, possible) <= robot.movementRange && !possible.equals(robot.previousLocation)) {
                        if (bc19.Helper.DistanceSquared(pos, possible) < closest) {
                            closest = bc19.Helper.DistanceSquared(pos, possible);
                            output = possible;
                        }
                    }
                }
                ;
            }
            ;
            return output;
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
        MovingRobot.prototype.GetOrCreateMap = function (robot, maps, goal, perfect) {
            if (goal == null) {
                return null;
            }
            if ((function (m, k) { if (m.entries == null)
                m.entries = []; for (var i = 0; i < m.entries.length; i++)
                if (m.entries[i].key.equals != null && m.entries[i].key.equals(k) || m.entries[i].key === k) {
                    return true;
                } return false; })(maps, goal)) {
                if ((function (m, k) { if (m.entries == null)
                    m.entries = []; for (var i = 0; i < m.entries.length; i++)
                    if (m.entries[i].key.equals != null && m.entries[i].key.equals(k) || m.entries[i].key === k) {
                        return m.entries[i].value;
                    } return null; })(maps, goal)[robot.me.y][robot.me.x] <= 0) {
                    var newMap = perfect ? MovingRobot.CreateLayeredFloodPath$bc19_MyRobot$bc19_Position(robot, goal) : MovingRobot.CreateLayeredFloodPath$bc19_MyRobot$bc19_Position$bc19_Position(robot, goal, robot.location);
                    /* put */ (function (m, k, v) { if (m.entries == null)
                        m.entries = []; for (var i = 0; i < m.entries.length; i++)
                        if (m.entries[i].key.equals != null && m.entries[i].key.equals(k) || m.entries[i].key === k) {
                            m.entries[i].value = v;
                            return;
                        } m.entries.push({ key: k, value: v, getKey: function () { return this.key; }, getValue: function () { return this.value; } }); })(maps, goal, newMap);
                    return newMap;
                }
                else {
                    return (function (m, k) { if (m.entries == null)
                        m.entries = []; for (var i = 0; i < m.entries.length; i++)
                        if (m.entries[i].key.equals != null && m.entries[i].key.equals(k) || m.entries[i].key === k) {
                            return m.entries[i].value;
                        } return null; })(maps, goal);
                }
            }
            else {
                var newMap = perfect ? MovingRobot.CreateLayeredFloodPath$bc19_MyRobot$bc19_Position(robot, goal) : MovingRobot.CreateLayeredFloodPath$bc19_MyRobot$bc19_Position$bc19_Position(robot, goal, robot.location);
                /* put */ (function (m, k, v) { if (m.entries == null)
                    m.entries = []; for (var i = 0; i < m.entries.length; i++)
                    if (m.entries[i].key.equals != null && m.entries[i].key.equals(k) || m.entries[i].key === k) {
                        m.entries[i].value = v;
                        return;
                    } m.entries.push({ key: k, value: v, getKey: function () { return this.key; }, getValue: function () { return this.value; } }); })(maps, goal, newMap);
                return newMap;
            }
        };
        MovingRobot.prototype.PathingDistance = function (robot, path) {
            return path[robot.me.y][robot.me.x];
        };
        MovingRobot.prototype.ReadPilgrimSignals = function (robot) {
            var outputRead = [0];
            var spawnStructure = robot.me;
            {
                var array124 = robot.getVisibleRobots();
                for (var index123 = 0; index123 < array124.length; index123++) {
                    var r = array124[index123];
                    {
                        if (bc19.Helper.DistanceSquared(robot.location, new bc19.Position(r.y, r.x)) <= 3) {
                            if (r.unit === robot.SPECS.CASTLE || r.unit === robot.SPECS.CHURCH) {
                                spawnStructure = r;
                            }
                        }
                    }
                }
            }
            var signal = spawnStructure.signal;
            if (signal !== -1 && signal <= 31) {
                var depotNum = signal & 31;
                outputRead[0] = depotNum;
                return outputRead;
            }
            else {
                outputRead[0] = -1;
                return outputRead;
            }
        };
        MovingRobot.prototype.ReadCombatSignals = function (robot, castleLocations) {
            var spawnStructure = robot.me;
            {
                var array126 = robot.getVisibleRobots();
                for (var index125 = 0; index125 < array126.length; index125++) {
                    var r = array126[index125];
                    {
                        if (bc19.Helper.DistanceSquared(robot.location, new bc19.Position(r.y, r.x)) <= 3) {
                            if (r.unit === robot.SPECS.CASTLE) {
                                spawnStructure = r;
                            }
                            else if (r.unit === robot.SPECS.CHURCH) {
                                /* add */ (castleLocations.push(new bc19.Position(r.y, r.x)) > 0);
                                return true;
                            }
                        }
                    }
                }
            }
            if (castleLocations.length === 0) {
                /* add */ (castleLocations.push(new bc19.Position(spawnStructure.y, spawnStructure.x)) > 0);
            }
            var signal = spawnStructure.signal;
            if (signal === -1) {
                return false;
            }
            var castle = this.CombatInitSignal(signal);
            if (castle != null && !bc19.Helper.ContainsPosition(castleLocations, castle)) {
                /* add */ (castleLocations.push(castle) > 0);
                return false;
            }
            else {
                return true;
            }
        };
        MovingRobot.prototype.CombatInitSignal = function (signal) {
            if (signal < 4096 || signal >= 8192) {
                return null;
            }
            var x = signal & 63;
            signal >>= 6;
            var y = signal & 63;
            return new bc19.Position(y, x);
        };
        MovingRobot.UpdateBattleStatus = function (robot, enemies, enemy) {
            var battleCry = MovingRobot.ListenForBattleCry(robot);
            if (battleCry != null) {
                if (bc19.Helper.ContainsPosition(enemies, battleCry)) {
                    return battleCry;
                }
                else {
                    /* add */ (enemies.push(battleCry) > 0);
                    return battleCry;
                }
            }
            return enemy;
        };
        MovingRobot.ListenForBattleCry = function (robot) {
            var robots = robot.getVisibleRobots();
            for (var i = 0; i < robots.length; i++) {
                var enemyCastle = MovingRobot.DecodeBattleCry(robot, robots[i].signal);
                if (enemyCastle != null) {
                    if (robot.me.unit !== robot.SPECS.PROPHET && robot.me.unit !== robot.SPECS.PREACHER) {
                        return enemyCastle;
                    }
                    else if (MovingRobot.ProphetBattleCry(robot, robots[i].signal)) {
                        return enemyCastle;
                    }
                }
            }
            ;
            return null;
        };
        MovingRobot.ProphetBattleCry = function (robot, signal) {
            if (signal <= 20479 && signal >= 16384) {
                return true;
            }
            return false;
        };
        MovingRobot.DecodeBattleCry = function (robot, signal) {
            if (signal > 20479 || signal < 8192) {
                return null;
            }
            var x = signal & 63;
            signal >>= 6;
            var y = signal & 63;
            return new bc19.Position(y, x);
        };
        MovingRobot.prototype.ListenForDefense = function (robot) {
            var robots = robot.getVisibleRobots();
            for (var i = 0; i < robots.length; i++) {
                var enemyAttacker = this.DecodeDefenseCry(robot, robots[i].signal);
                if (enemyAttacker != null) {
                    return enemyAttacker;
                }
            }
            ;
            return null;
        };
        MovingRobot.prototype.EnemiesOfTypeInVision = function (robot, type) {
            var robots = robot.getVisibleRobots();
            var output = ([]);
            for (var i = 0; i < robots.length; i++) {
                var r = robots[i];
                if (bc19.Helper.DistanceSquared(new bc19.Position(r.y, r.x), robot.location) <= robot.visionRange) {
                    if (r.team !== robot.ourTeam) {
                        for (var j = 0; j < type.length; j++) {
                            if (r.unit === type[j]) {
                                /* add */ (output.push(r) > 0);
                            }
                        }
                        ;
                    }
                }
            }
            ;
            return output;
        };
        MovingRobot.prototype.DecodeDefenseCry = function (robot, signal) {
            if (signal > 36863 || signal < 32768) {
                return null;
            }
            var x = signal & 63;
            signal >>= 6;
            var y = signal & 63;
            return new bc19.Position(y, x);
        };
        MovingRobot.prototype.GetValidDefense = function (robot, routesToEnemy, parent, invader) {
            var valid = ([]);
            for (var y = -7; y <= 7; y++) {
                for (var x = -7; x <= 7; x++) {
                    var possible = new bc19.Position(parent.y + y, parent.x + x);
                    if (bc19.Helper.BetweenTwoPoints(robot, possible, invader, parent) && bc19.Helper.DistanceSquared(robot.location, possible) <= 49 && bc19.Helper.inMap(robot.map, possible)) {
                        if (robot.getKarboniteMap()[possible.y][possible.x] || robot.getFuelMap()[possible.y][possible.x]) {
                            continue;
                        }
                        if (bc19.Helper.TileEmpty(robot, possible) && !bc19.Helper.IsSurroundingsOccupied(robot, possible)) {
                            if ((Math.abs(possible.y - parent.y) % 2 === 0) && (Math.abs(possible.x - parent.x) % 2 === 0)) {
                                /* add */ (valid.push(possible) > 0);
                            }
                            else if ((Math.abs(possible.y - parent.y) % 2 === 1) && (Math.abs(possible.x - parent.x) % 2 === 1)) {
                                /* add */ (valid.push(possible) > 0);
                            }
                        }
                    }
                }
                ;
            }
            ;
            return valid;
        };
        MovingRobot.prototype.Fortified = function (robot, tile) {
            if (robot.getKarboniteMap()[tile.y][tile.x] || robot.getFuelMap()[tile.y][tile.x] || bc19.StationairyRobot.UnitAround(robot, tile, 2, robot.SPECS.CHURCH) > 0 || bc19.StationairyRobot.UnitAround(robot, tile, 2, robot.SPECS.CASTLE) > 0) {
                return false;
            }
            if ((Math.abs(tile.y) % 2 === 0) && (Math.abs(tile.x) % 2 === 0)) {
                return true;
            }
            if ((Math.abs(tile.y) % 2 === 1) && (Math.abs(tile.x) % 2 === 1)) {
                return true;
            }
            return false;
        };
        MovingRobot.prototype.TowardsCenter = function (robot) {
            var center = new bc19.Position((robot.map.length / 2 | 0), (robot.map.length / 2 | 0));
            var ySign = bc19.Helper.sign(center.y - robot.me.y);
            var xSign = bc19.Helper.sign(center.x - robot.me.x);
            return new bc19.Position(robot.me.y + (ySign * 5), robot.me.x + (xSign * 5));
        };
        MovingRobot.prototype.GetValidFortifiedPositions = function (robot, parent) {
            var valid = ([]);
            for (var y = -robot.tileVisionRange; y <= robot.tileVisionRange; y++) {
                for (var x = -robot.tileVisionRange; x <= robot.tileVisionRange; x++) {
                    var possible = new bc19.Position(robot.me.y + y, robot.me.x + x);
                    if (bc19.Helper.DistanceSquared(robot.location, possible) <= robot.visionRange && bc19.Helper.TileEmpty(robot, possible)) {
                        if (!this.Fortified(robot, possible)) {
                            continue;
                        }
                        if (((possible.y) % 2 === 0) && ((possible.x) % 2 === 0)) {
                            /* add */ (valid.push(possible) > 0);
                        }
                        else if (((possible.y) % 2 === 1) && ((possible.x) % 2 === 1)) {
                            /* add */ (valid.push(possible) > 0);
                        }
                    }
                }
                ;
            }
            ;
            return valid;
        };
        MovingRobot.prototype.BlackOutPaths = function (robot, flood, units) {
            var output = bc19.Helper.twoDimensionalArrayClone(flood);
            for (var i = 0; i < units.length; i++) {
                var unit = new bc19.Position(/* get */ units[i].y, /* get */ units[i].x);
                var attackRange = (Math.sqrt(robot.SPECS.UNITS[units[i].unit].VISION_RADIUS) | 0);
                for (var j = -attackRange; j <= attackRange; j++) {
                    for (var k = -attackRange; k <= attackRange; k++) {
                        var relative = new bc19.Position(unit.y + j, unit.x + k);
                        if (bc19.Helper.inMap(robot.map, relative) && bc19.Helper.DistanceSquared(unit, relative) <= robot.SPECS.UNITS[units[i].unit].VISION_RADIUS) {
                            output[relative.y][relative.x] = 999;
                        }
                    }
                    ;
                }
                ;
            }
            ;
            return output;
        };
        MovingRobot.prototype.CastleDown = function (robot, enemyCastleLocations, routesToEnemies) {
            var robots = robot.getVisibleRobots();
            for (var i = enemyCastleLocations.length - 1; i >= 0; i--) {
                for (var j = 0; j < robots.length; j++) {
                    if (this.StructureGone(robot, /* get */ enemyCastleLocations[i])) {
                        /* remove */ (function (m, k) { if (m.entries == null)
                            m.entries = []; for (var i_1 = 0; i_1 < m.entries.length; i_1++)
                            if (m.entries[i_1].key.equals != null && m.entries[i_1].key.equals(k) || m.entries[i_1].key === k) {
                                return m.entries.splice(i_1, 1)[0];
                            } })(routesToEnemies, /* get */ enemyCastleLocations[i]);
                        /* remove */ enemyCastleLocations.splice(i, 1);
                    }
                }
                ;
            }
            ;
        };
        MovingRobot.prototype.StructureGone = function (robot, structure) {
            if (bc19.Helper.DistanceSquared(structure, robot.location) <= robot.visionRange) {
                if (bc19.Helper.TileEmpty(robot, structure)) {
                    return true;
                }
                else if (bc19.Helper.RobotAtPosition(robot, structure).unit !== robot.SPECS.CASTLE) {
                    return true;
                }
            }
            return false;
        };
        MovingRobot.prototype.ThreatsAround = function (robot) {
            var robots = robot.getVisibleRobots();
            for (var i = 0; i < robots.length; i++) {
                if (robots[i].team !== robot.ourTeam && robots[i].unit !== robot.SPECS.PILGRIM && robots[i].unit !== robot.SPECS.CHURCH) {
                    var distance = bc19.Helper.DistanceSquared(robot.location, new bc19.Position(robots[i].y, robots[i].x));
                    if (distance <= robot.visionRange && distance <= robot.SPECS.UNITS[robots[i].unit].ATTACK_RADIUS[1]) {
                        return true;
                    }
                }
            }
            ;
            return false;
        };
        MovingRobot.prototype.StructureBornFrom = function (robot) {
            var robots = robot.getVisibleRobots();
            for (var i = 0; i < robots.length; i++) {
                var robotPosition = new bc19.Position(robots[i].y, robots[i].x);
                if (robots[i].team === robot.ourTeam && (robots[i].unit === robot.SPECS.CHURCH || robots[i].unit === robot.SPECS.CASTLE)) {
                    if (bc19.Helper.DistanceSquared(robot.location, robotPosition) <= 3) {
                        return robots[i];
                    }
                }
            }
            ;
            return null;
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
    var Helper = (function () {
        function Helper() {
        }
        Helper.inMap = function (map, pos) {
            if (pos.y < 0 || pos.y > (map.length - 1) || pos.x < 0 || pos.x > (map[0].length - 1)) {
                return false;
            }
            return true;
        };
        Helper.AllOpenInRange = function (robot, pos, tileRange, moveRange) {
            var validPositions = ([]);
            for (var i = -tileRange; i <= tileRange; i++) {
                for (var j = -tileRange; j <= tileRange; j++) {
                    var relative = new bc19.Position(pos.y + i, pos.x + j);
                    if (Helper.TileEmpty(robot, relative) && Helper.DistanceSquared(pos, relative) <= moveRange) {
                        /* add */ (validPositions.push(relative) > 0);
                    }
                }
                ;
            }
            ;
            return validPositions;
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
        Helper.EnemiesAround = function (robot) {
            var robots = robot.getVisibleRobots();
            for (var i = 0; i < robots.length; i++) {
                if (robots[i].team !== robot.ourTeam) {
                    return true;
                }
            }
            ;
            return false;
        };
        Helper.abs = function (a) {
            return a < 0 ? -a : a;
        };
        Helper.sign = function (a) {
            return a === 0 ? 0 : (a / Helper.abs(a) | 0);
        };
        Helper.ContainsPosition = function (list, pos) {
            for (var i = 0; i < list.length; i++) {
                if (list[i].y === pos.y && list[i].x === pos.x) {
                    return true;
                }
            }
            ;
            return false;
        };
        Helper.ClosestPosition = function (robot, positions) {
            var dist = Number.MAX_VALUE;
            var closest = null;
            for (var i = 0; i < positions.length; i++) {
                if (Helper.DistanceSquared(/* get */ positions[i], robot.location) < dist) {
                    dist = Helper.DistanceSquared(/* get */ positions[i], robot.location);
                    closest = positions[i];
                }
            }
            ;
            return closest;
        };
        Helper.Have = function (robot, karbonite, fuel) {
            if (robot.karbonite >= karbonite && robot.fuel >= fuel) {
                return true;
            }
            return false;
        };
        Helper.closestEnemy = function (robot, robots) {
            var dist = Number.MAX_VALUE;
            var closest = null;
            for (var i = 0; i < robots.length; i++) {
                var rp = new bc19.Position(/* get */ robots[i].y, /* get */ robots[i].x);
                if (Helper.DistanceSquared(rp, robot.location) < dist) {
                    dist = Helper.DistanceSquared(rp, robot.location);
                    closest = rp;
                }
            }
            ;
            return closest;
        };
        Helper.EnemiesWithin = function (robot, range) {
            var output = ([]);
            var robots = robot.getVisibleRobots();
            for (var i = 0; i < robots.length; i++) {
                if (robots[i].team !== robot.ourTeam && Helper.DistanceSquared(new bc19.Position(robots[i].y, robots[i].x), robot.location) <= range) {
                    /* add */ (output.push(robots[i]) > 0);
                }
            }
            ;
            return output;
        };
        Helper.BetweenTwoPoints = function (robot, point, pos1, pos2) {
            var vector1to2 = new bc19.Position(pos2.y - pos1.y, pos2.x - pos1.x);
            var vector2to1 = new bc19.Position(pos1.y - pos2.y, pos1.x - pos2.x);
            var onePerpVec = new bc19.Position(vector1to2.x, vector1to2.y * -1);
            var twoPerpVec = new bc19.Position(vector2to1.x, vector2to1.y * -1);
            var pos1SecP = new bc19.Position(pos1.y + onePerpVec.y, pos1.x + onePerpVec.x);
            var pos2SecP = new bc19.Position(pos2.y + twoPerpVec.y, pos2.x + twoPerpVec.x);
            if (!Helper.leftOfLine(robot, point, pos1SecP, pos1) && Helper.leftOfLine(robot, point, pos2, pos2SecP)) {
                return true;
            }
            return false;
        };
        Helper.leftOfLine = function (robot, point, pos1, pos2) {
            return (((pos2.x - pos1.x) * (pos1.y - point.y)) - ((pos1.y - pos2.y) * (point.x - pos1.x))) >= 0;
        };
        Helper.IsSurroundingsOccupied = function (robot, pos) {
            for (var i = -1; i <= 1; i++) {
                for (var j = -1; j <= 1; j++) {
                    var relative = new bc19.Position(pos.y + i, pos.x + j);
                    var atRelative = Helper.RobotAtPosition(robot, relative);
                    if (atRelative != null && atRelative.team === robot.ourTeam && atRelative.id !== robot.id) {
                        return true;
                    }
                }
                ;
            }
            ;
            return false;
        };
        Helper.RandomAdjacentNonResource = function (robot, pos) {
            var lowest = Number.MAX_VALUE;
            var best = null;
            for (var i = -1; i <= 1; i++) {
                for (var j = -1; j <= 1; j++) {
                    var adjacent = new bc19.Position(pos.y + i, pos.x + j);
                    if (Helper.TileEmptyNonResource(robot, adjacent)) {
                        var count = 0;
                        for (var k = -1; k <= 1; k++) {
                            for (var l = -1; l <= 1; l++) {
                                if (!Helper.TileEmpty(robot, new bc19.Position(adjacent.y + k, adjacent.x + l))) {
                                    count++;
                                }
                            }
                            ;
                        }
                        ;
                        if (count < lowest) {
                            lowest = count;
                            best = adjacent;
                        }
                    }
                }
                ;
            }
            ;
            return best;
        };
        Helper.RandomAdjacent = function (robot, pos) {
            var lowest = Number.MAX_VALUE;
            var best = null;
            for (var i = -1; i <= 1; i++) {
                for (var j = -1; j <= 1; j++) {
                    var adjacent = new bc19.Position(pos.y + i, pos.x + j);
                    if (Helper.TileEmpty(robot, adjacent)) {
                        var count = 9;
                        for (var k = -1; k <= 1; k++) {
                            for (var l = -1; l <= 1; l++) {
                                if (Helper.TileEmpty(robot, new bc19.Position(adjacent.y + k, adjacent.x + l))) {
                                    count--;
                                }
                            }
                            ;
                        }
                        ;
                        if (count < lowest) {
                            lowest = count;
                            best = adjacent;
                        }
                    }
                }
                ;
            }
            ;
            return best;
        };
        Helper.RandomAdjacentMoveable = function (robot, pos, moveRange) {
            for (var i = -1; i <= 1; i++) {
                for (var j = -1; j <= 1; j++) {
                    var adjacent = new bc19.Position(pos.y + i, pos.x + j);
                    if (Helper.TileEmpty(robot, adjacent) && Helper.DistanceSquared(robot.location, adjacent) <= moveRange) {
                        return new bc19.Position(adjacent.y, adjacent.x);
                    }
                }
                ;
            }
            ;
            return null;
        };
        Helper.twoDimensionalArrayClone = function (original) {
            var output = (function (dims) { var allocate = function (dims) { if (dims.length == 0) {
                return 0;
            }
            else {
                var array = [];
                for (var i = 0; i < dims[0]; i++) {
                    array.push(allocate(dims.slice(1)));
                }
                return array;
            } }; return allocate(dims); })([original.length, original[0].length]);
            for (var i = 0; i < original.length; i++) {
                output[i] = original[i].slice(0);
            }
            ;
            return output;
        };
        Helper.TileEmptyNonResource = function (robot, pos) {
            if (Helper.TileEmpty(robot, pos) && !robot.getKarboniteMap()[pos.y][pos.x] && !robot.getFuelMap()[pos.y][pos.x]) {
                return true;
            }
            return false;
        };
        Helper.TileEmpty = function (robot, pos) {
            if (Helper.inMap(robot.map, pos) && robot.map[pos.y][pos.x] && robot.getVisibleRobotMap()[pos.y][pos.x] <= 0) {
                return true;
            }
            return false;
        };
        Helper.CanAfford = function (robot, unit) {
            if (robot.karbonite > robot.SPECS.UNITS[unit].CONSTRUCTION_KARBONITE && robot.fuel > robot.SPECS.UNITS[unit].CONSTRUCTION_FUEL) {
                return true;
            }
            return false;
        };
        Helper.PositiveOrNegativeMap = function (robot) {
            if (robot.mapIsHorizontal) {
                return (robot.me.y < (((robot.map.length + 1) / 2 | 0))) ? true : false;
            }
            else {
                return (robot.me.x > (((robot.map[0].length + 1) / 2 | 0))) ? true : false;
            }
        };
        Helper.ResourcesOnOurHalfMap = function (robot) {
            var halfResourceMap = robot.mapIsHorizontal ? (function (dims) { var allocate = function (dims) { if (dims.length == 0) {
                return undefined;
            }
            else {
                var array = [];
                for (var i = 0; i < dims[0]; i++) {
                    array.push(allocate(dims.slice(1)));
                }
                return array;
            } }; return allocate(dims); })([((robot.map.length + 1) / 2 | 0), robot.map.length]) : (function (dims) { var allocate = function (dims) { if (dims.length == 0) {
                return undefined;
            }
            else {
                var array = [];
                for (var i = 0; i < dims[0]; i++) {
                    array.push(allocate(dims.slice(1)));
                }
                return array;
            } }; return allocate(dims); })([robot.map.length, ((robot.map.length + 1) / 2 | 0)]);
            if (robot.positiveSide && robot.mapIsHorizontal) {
                for (var i = 0; i < ((robot.map.length + 1) / 2 | 0); i++) {
                    for (var j = 0; j < robot.map[0].length; j++) {
                        halfResourceMap[i][j] = robot.getFuelMap()[i][j] || robot.getKarboniteMap()[i][j] ? true : false;
                    }
                    ;
                }
                ;
            }
            else if (!robot.positiveSide && robot.mapIsHorizontal) {
                for (var i = ((robot.map.length) / 2 | 0); i < robot.map.length; i++) {
                    for (var j = 0; j < robot.map[0].length; j++) {
                        halfResourceMap[i - ((robot.map[0].length) / 2 | 0)][j] = robot.getFuelMap()[i][j] || robot.getKarboniteMap()[i][j] ? true : false;
                    }
                    ;
                }
                ;
            }
            else if (robot.positiveSide && !robot.mapIsHorizontal) {
                for (var i = 0; i < robot.map.length; i++) {
                    for (var j = ((robot.map[0].length) / 2 | 0); j < robot.map[i].length; j++) {
                        halfResourceMap[i][j - ((robot.map[0].length) / 2 | 0)] = robot.getFuelMap()[i][j] || robot.getKarboniteMap()[i][j] ? true : false;
                    }
                    ;
                }
                ;
            }
            else if (!robot.positiveSide && !robot.mapIsHorizontal) {
                for (var i = 0; i < robot.map.length; i++) {
                    for (var j = 0; j < ((robot.map[0].length + 1) / 2 | 0); j++) {
                        halfResourceMap[i][j] = robot.getFuelMap()[i][j] || robot.getKarboniteMap()[i][j] ? true : false;
                    }
                    ;
                }
                ;
            }
            return halfResourceMap;
        };
        Helper.FindClusters = function (robot, resourceMap) {
            var output = ([]);
            var yAdd = 0;
            var xAdd = 0;
            if (robot.mapIsHorizontal && !robot.positiveSide) {
                yAdd = ((robot.map.length) / 2 | 0);
            }
            else if (!robot.mapIsHorizontal && robot.positiveSide) {
                xAdd = ((robot.map.length) / 2 | 0);
            }
            for (var y = 0; y < resourceMap.length; y++) {
                for (var x = 0; x < resourceMap[y].length; x++) {
                    if (resourceMap[y][x]) {
                        var cluster = new bc19.ResourceCluster();
                        /* add */ (cluster.resourceLocations.push(new bc19.Position(y + yAdd, x + xAdd)) > 0);
                        resourceMap[y][x] = false;
                        for (var i = 0; i <= 4; i++) {
                            for (var j = -4; j <= 4; j++) {
                                if (Helper.inMap(resourceMap, new bc19.Position(y + i, x + j)) && resourceMap[y + i][x + j]) {
                                    /* add */ (cluster.resourceLocations.push(new bc19.Position(y + yAdd + i, x + xAdd + j)) > 0);
                                    resourceMap[y + i][x + j] = false;
                                }
                            }
                            ;
                        }
                        ;
                        /* add */ (output.push(cluster) > 0);
                    }
                }
                ;
            }
            ;
            return output;
        };
        Helper.ChurchLocationsFromClusters = function (robot, clusters) {
            var churchLocations = ([]);
            for (var i = 0; i < clusters.length; i++) {
                var cluster = clusters[i];
                var yAvg = 0;
                var xAvg = 0;
                for (var j = 0; j < cluster.resourceLocations.length; j++) {
                    var resource = cluster.resourceLocations[j];
                    yAvg += resource.y;
                    xAvg += resource.x;
                }
                ;
                yAvg /= cluster.resourceLocations.length;
                xAvg /= cluster.resourceLocations.length;
                var center = new bc19.Position(Math.round(yAvg), Math.round(xAvg));
                var lowestDist = Number.MAX_VALUE;
                var nonResourceCenter = center;
                for (var y = -2; y <= 2; y++) {
                    for (var x = -2; x <= 2; x++) {
                        var pos = new bc19.Position(center.y + y, center.x + x);
                        var sum = 0;
                        for (var d = 0; d < cluster.resourceLocations.length; d++) {
                            sum += Helper.DistanceSquared(pos, /* get */ cluster.resourceLocations[d]);
                        }
                        ;
                        if (!Helper.ContainsPosition(/* get */ clusters[i].resourceLocations, pos) && sum < lowestDist) {
                            lowestDist = sum;
                            nonResourceCenter = pos;
                        }
                    }
                    ;
                }
                ;
                /* add */ (churchLocations.push(nonResourceCenter) > 0);
            }
            ;
            return churchLocations;
        };
        return Helper;
    }());
    bc19.Helper = Helper;
    Helper["__class"] = "bc19.Helper";
    var ResourceCluster = (function () {
        function ResourceCluster() {
            this.resourceLocations = null;
            this.resourceLocations = ([]);
        }
        return ResourceCluster;
    }());
    bc19.ResourceCluster = ResourceCluster;
    ResourceCluster["__class"] = "bc19.ResourceCluster";
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
    var StationairyRobot = (function () {
        function StationairyRobot() {
        }
        StationairyRobot.prototype.EvaluateEnemyRatio = function (robot) {
            var robots = robot.getVisibleRobots();
            var enemyPreacherCrusader = 0;
            var enemyProphet = 0;
            var enemyPassive = 0;
            for (var i = 0; i < robots.length; i++) {
                var r = robots[i];
                if (bc19.Helper.DistanceSquared(new bc19.Position(r.y, r.x), robot.location) <= robot.visionRange) {
                    if (r.team !== robot.ourTeam) {
                        if (r.unit === robot.SPECS.PREACHER) {
                            enemyPreacherCrusader++;
                        }
                        else if (r.unit === robot.SPECS.CRUSADER) {
                            enemyPreacherCrusader++;
                        }
                        else if (r.unit === robot.SPECS.PROPHET) {
                            enemyProphet++;
                        }
                        else {
                            enemyPassive = 1;
                        }
                    }
                    else if (bc19.Helper.DistanceSquared(new bc19.Position(r.y, r.x), robot.location) <= 36) {
                        if (r.unit === robot.SPECS.PREACHER) {
                            enemyPreacherCrusader--;
                        }
                        else if (r.unit === robot.SPECS.CRUSADER) {
                            enemyProphet--;
                            enemyPassive = -10;
                        }
                    }
                }
            }
            ;
            if (enemyPreacherCrusader > 0 && bc19.Helper.Have(robot, 30, 60)) {
                var random = StationairyRobot.RandomAdjacentTowardsEnemy(robot, bc19.Helper.closestEnemy(robot, bc19.Helper.EnemiesWithin(robot, robot.visionRange)));
                return robot.buildUnit(robot.SPECS.PREACHER, random.x - robot.me.x, random.y - robot.me.y);
            }
            else if ((enemyProphet > 0 || enemyPassive > 0) && bc19.Helper.Have(robot, 15, 60)) {
                var random = StationairyRobot.RandomAdjacentTowardsEnemy(robot, bc19.Helper.closestEnemy(robot, bc19.Helper.EnemiesWithin(robot, robot.visionRange)));
                return robot.buildUnit(robot.SPECS.CRUSADER, random.x - robot.me.x, random.y - robot.me.y);
            }
            else
                return null;
        };
        StationairyRobot.RandomAdjacentTowardsEnemy = function (robot, enemy) {
            var lowest = Number.MAX_VALUE;
            var best = null;
            for (var i = -1; i <= 1; i++) {
                for (var j = -1; j <= 1; j++) {
                    var adjacent = new bc19.Position(robot.me.y + i, robot.me.x + j);
                    if (bc19.Helper.TileEmpty(robot, adjacent)) {
                        var distance = bc19.Helper.DistanceSquared(adjacent, enemy);
                        if (distance < lowest) {
                            lowest = distance;
                            best = adjacent;
                        }
                    }
                }
                ;
            }
            ;
            return best;
        };
        StationairyRobot.prototype.CreateAttackSignal = function (pos, code) {
            var output = code;
            output <<= 6;
            output += pos.y;
            output <<= 6;
            output += pos.x;
            return output;
        };
        StationairyRobot.prototype.ResourcesAround = function (robot, tileRadius) {
            var numResources = 0;
            for (var i = -tileRadius; i <= tileRadius; i++) {
                for (var j = -tileRadius; j <= tileRadius; j++) {
                    var relative = new bc19.Position(robot.me.y + i, robot.me.x + j);
                    if (bc19.Helper.inMap(robot.map, relative) && bc19.Helper.DistanceSquared(robot.location, relative) < robot.visionRange) {
                        if (robot.getFuelMap()[relative.y][relative.x] || robot.getKarboniteMap()[relative.y][relative.x]) {
                            numResources++;
                        }
                    }
                }
                ;
            }
            ;
            return numResources;
        };
        StationairyRobot.UnitAround = function (robot, center, tileRadius, unitType) {
            var numPilgrims = 0;
            for (var i = -tileRadius; i <= tileRadius; i++) {
                for (var j = -tileRadius; j <= tileRadius; j++) {
                    var relative = new bc19.Position(center.y + i, center.x + j);
                    if (bc19.Helper.inMap(robot.map, relative)) {
                        var onTile = bc19.Helper.RobotAtPosition(robot, relative);
                        if (onTile != null && onTile.unit === unitType && onTile.team === robot.ourTeam) {
                            numPilgrims++;
                        }
                    }
                }
                ;
            }
            ;
            return numPilgrims;
        };
        return StationairyRobot;
    }());
    bc19.StationairyRobot = StationairyRobot;
    StationairyRobot["__class"] = "bc19.StationairyRobot";
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
            var fuelNeeded = (Math.ceil(Math.sqrt(radius)) | 0);
            if (this.fuel < fuelNeeded)
                throw new bc19.BCException("Not enough fuel to signal given radius.");
            if (value < 0 || value >= Math.pow(2, this.SPECS.COMMUNICATION_BITS))
                throw new bc19.BCException("Invalid signal, must be within bit range.");
            if (radius > 2 * Math.pow(this.SPECS.MAX_BOARD_SIZE - 1, 2))
                throw new bc19.BCException("Signal radius is too big.");
            this.__signal = value;
            this.signalRadius = radius;
            this.fuel -= fuelNeeded;
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
            if (this.me.unit === this.SPECS.CHURCH)
                throw new bc19.BCException("Churches cannot attack.");
            if (this.fuel < this.SPECS.UNITS[this.me.unit].ATTACK_FUEL_COST)
                throw new bc19.BCException("Not enough fuel to attack.");
            if (!this.checkOnMap(this.me.x + dx, this.me.y + dy))
                throw new bc19.BCException("Can\'t attack off of map.");
            if (this.gameState.shadow[this.me.y + dy][this.me.x + dx] === -1)
                throw new bc19.BCException("Cannot attack outside of vision range.");
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
            _this.initialized = false;
            _this.parent = null;
            _this.parentLocation = null;
            _this.targetCastle = null;
            _this.castleLocations = null;
            _this.enemyCastleLocations = null;
            _this.routesToEnemies = null;
            _this.manualFort = false;
            _this.fortCount = 0;
            _this.robot = robot;
            return _this;
        }
        Crusader.prototype.Execute = function () {
            this.robot.log("Crusader : " + this.robot.location);
            if (this.robot.me.turn === 1) {
                this.InitializeVariables();
                this.parent = this.StructureBornFrom(this.robot);
                this.parentLocation = new bc19.Position(this.parent.y, this.parent.x);
                if (this.parent.unit === this.robot.SPECS.CHURCH) {
                    this.initialized = true;
                }
            }
            if (!this.initialized) {
                this.CastleInit();
            }
            this.targetCastle = bc19.MovingRobot.UpdateBattleStatus(this.robot, this.enemyCastleLocations, this.targetCastle);
            var invader = this.ListenForDefense(this.robot);
            if (bc19.Helper.Have(this.robot, 0, 50)) {
                if (bc19.Helper.EnemiesAround(this.robot)) {
                    var prophets = this.EnemiesOfTypeInVision(this.robot, [this.robot.SPECS.PROPHET]);
                    if (prophets.length > 0) {
                        var farthest = this.FarthestProphetOutOfRange(prophets);
                        var closest = bc19.Helper.closestEnemy(this.robot, prophets);
                        if (farthest != null && this.initialized && bc19.Helper.DistanceSquared(closest, this.robot.location) > 9) {
                            return bc19.MovingRobot.MoveCloser(this.robot, new bc19.Position(farthest.y, farthest.x), false);
                        }
                        else {
                            return this.AttackEnemies(/* toArray */ prophets.slice(0));
                        }
                    }
                    var crusadersAndHarmless = this.EnemiesOfTypeInVision(this.robot, [this.robot.SPECS.CHURCH, this.robot.SPECS.CASTLE, this.robot.SPECS.PILGRIM, this.robot.SPECS.CRUSADER]);
                    if (crusadersAndHarmless.length > 0) {
                        var withinAttackRange = this.InAttackRange(crusadersAndHarmless);
                        if (withinAttackRange.length > 0) {
                            return this.AttackEnemies(/* toArray */ withinAttackRange.slice(0));
                        }
                        else if (this.initialized) {
                            var farthest = this.FarthestProphetOutOfRange(crusadersAndHarmless);
                            return bc19.MovingRobot.MoveCloser(this.robot, new bc19.Position(farthest.y, farthest.x), false);
                        }
                    }
                }
                else if (invader != null) {
                    var shortPath = bc19.MovingRobot.CreateLayeredFloodPath$bc19_MyRobot$bc19_Position$bc19_Position(this.robot, invader, this.robot.location);
                    return bc19.MovingRobot.FloodPathing(this.robot, shortPath, invader, false);
                }
            }
            if (this.initialized && bc19.Helper.Have(this.robot, 0, 325)) {
                if (this.targetCastle == null && !this.Fortified(this.robot, this.robot.location) && !this.manualFort) {
                    this.fortCount++;
                    if (this.fortCount > 10) {
                        this.manualFort = true;
                    }
                    var valid = this.GetValidFortifiedPositions(this.robot, this.parentLocation);
                    if (valid.length > 0) {
                        var closest = bc19.Helper.ClosestPosition(this.robot, valid);
                        var shortPath = bc19.MovingRobot.CreateLayeredFloodPath$bc19_MyRobot$bc19_Position$bc19_Position(this.robot, closest, this.robot.location);
                        return bc19.MovingRobot.FloodPathing(this.robot, shortPath, closest, false);
                    }
                    else {
                        var goal = null;
                        if (this.robot.mapIsHorizontal) {
                            if (this.robot.me.y > (this.robot.map.length / 2 | 0)) {
                                goal = new bc19.Position(0, this.robot.me.x);
                            }
                            else {
                                goal = new bc19.Position(this.robot.map.length - 1, this.robot.me.x);
                            }
                        }
                        else {
                            if (this.robot.me.x > (this.robot.map.length / 2 | 0)) {
                                goal = new bc19.Position(this.robot.me.y, 0);
                            }
                            else {
                                goal = new bc19.Position(this.robot.me.y, this.robot.map.length - 1);
                            }
                        }
                        return bc19.MovingRobot.FloodPathing(this.robot, this.GetOrCreateMap(this.robot, this.routesToEnemies, goal, false), goal, false);
                    }
                }
                else if (this.targetCastle != null) {
                    var rushTime = true;
                    this.CastleDown(this.robot, this.enemyCastleLocations, this.routesToEnemies);
                    var preachers = this.EnemiesOfTypeInVision(this.robot, [this.robot.SPECS.PREACHER]);
                    if (bc19.Helper.ContainsPosition(this.enemyCastleLocations, this.targetCastle)) {
                        if (bc19.Helper.DistanceSquared(this.robot.location, this.targetCastle) <= 196) {
                            rushTime = false;
                        }
                        var pathingMap = bc19.Helper.twoDimensionalArrayClone(this.GetOrCreateMap(this.robot, this.routesToEnemies, this.targetCastle, true));
                        if (preachers.length > 0) {
                            pathingMap = this.BlackOutPreacherPaths(pathingMap, preachers);
                        }
                        return bc19.MovingRobot.FloodPathing(this.robot, pathingMap, this.targetCastle, rushTime);
                    }
                    else {
                        var closestEnemyCastle = this.ClosestEnemyCastle(this.robot, this.routesToEnemies);
                        if (closestEnemyCastle != null && bc19.Helper.DistanceSquared(this.robot.location, closestEnemyCastle) <= 196) {
                            rushTime = false;
                        }
                        var pathingMap = bc19.Helper.twoDimensionalArrayClone(this.GetOrCreateMap(this.robot, this.routesToEnemies, closestEnemyCastle, true));
                        if (preachers.length > 0) {
                            pathingMap = this.BlackOutPreacherPaths(pathingMap, preachers);
                        }
                        return bc19.MovingRobot.FloodPathing(this.robot, pathingMap, closestEnemyCastle, rushTime);
                    }
                }
            }
            return null;
        };
        Crusader.prototype.CastleInit = function () {
            this.initialized = this.ReadCombatSignals(this.robot, this.castleLocations);
            if (this.initialized) {
                this.enemyCastleLocations = bc19.Helper.FindEnemyCastles(this.robot, this.robot.mapIsHorizontal, this.castleLocations);
                for (var i = 0; i < this.enemyCastleLocations.length; i++) {
                    this.GetOrCreateMap(this.robot, this.routesToEnemies, /* get */ this.enemyCastleLocations[i], false);
                }
                ;
            }
        };
        Crusader.prototype.InitializeVariables = function () {
            this.castleLocations = ([]);
            this.enemyCastleLocations = ([]);
            this.routesToEnemies = ({});
        };
        Crusader.prototype.AttackEnemies = function (robots) {
            var attackTile = null;
            var lowestID = Number.MAX_VALUE;
            for (var i = 0; i < robots.length; i++) {
                var robotPos = new bc19.Position(robots[i].y, robots[i].x);
                if (robots[i].team !== this.robot.ourTeam && bc19.Helper.DistanceSquared(robotPos, this.robot.location) <= this.robot.attackRange[1]) {
                    if (robots[i].id < lowestID) {
                        lowestID = robots[i].id;
                        attackTile = robotPos;
                    }
                }
            }
            ;
            return this.robot.attack(attackTile.x - this.robot.me.x, attackTile.y - this.robot.me.y);
        };
        Crusader.prototype.FarthestProphetOutOfRange = function (prophets) {
            var furthestDist = 0;
            var furthest = null;
            for (var i = 0; i < prophets.length; i++) {
                var distance = bc19.Helper.DistanceSquared(this.robot.location, new bc19.Position(/* get */ prophets[i].y, /* get */ prophets[i].x));
                if (distance > this.robot.SPECS.UNITS[this.robot.SPECS.PROPHET].ATTACK_RADIUS[0]) {
                    if (distance > furthestDist) {
                        furthestDist = distance;
                        furthest = prophets[i];
                    }
                }
            }
            ;
            return furthest;
        };
        Crusader.prototype.InAttackRange = function (robots) {
            var output = ([]);
            for (var i = 0; i < robots.length; i++) {
                var r = robots[i];
                if (bc19.Helper.DistanceSquared(new bc19.Position(r.y, r.x), this.robot.location) <= this.robot.attackRange[1]) {
                    /* add */ (output.push(r) > 0);
                }
            }
            ;
            return output;
        };
        Crusader.prototype.BlackOutPreacherPaths = function (flood, preachers) {
            var output = bc19.Helper.twoDimensionalArrayClone(flood);
            var preacherAttackRange = (Math.sqrt(this.robot.SPECS.UNITS[this.robot.SPECS.PREACHER].ATTACK_RADIUS[1]) | 0);
            for (var i = 0; i < preachers.length; i++) {
                var preach = new bc19.Position(/* get */ preachers[i].y, /* get */ preachers[i].x);
                for (var j = -preacherAttackRange; j <= preacherAttackRange; j++) {
                    for (var k = -preacherAttackRange; k <= preacherAttackRange; k++) {
                        var pos = new bc19.Position(preach.y + j, preach.x + k);
                        if (bc19.Helper.inMap(this.robot.map, pos) && bc19.Helper.DistanceSquared(preach, pos) <= this.robot.SPECS.UNITS[this.robot.SPECS.PREACHER].ATTACK_RADIUS[1]) {
                            output[pos.y][pos.x] = 999;
                        }
                    }
                    ;
                }
                ;
            }
            ;
            return output;
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
            _this.robot = null;
            _this.state = null;
            _this.depotNum = 0;
            _this.spawnLocation = null;
            _this.churchLocation = null;
            _this.counter = 0;
            _this.karbLocations = null;
            _this.fuelLocations = null;
            _this.allRoutes = null;
            _this.occupiedResources = null;
            _this.robot = robot;
            return _this;
        }
        Pilgrim.prototype.Execute = function () {
            this.robot.log("Pilgrim : " + this.robot.location);
            if (this.robot.me.turn === 1) {
                this.InitializeVariables();
                var spawn = this.StructureBornFrom(this.robot);
                this.spawnLocation = new bc19.Position(spawn.y, spawn.x);
                this.Initialize();
            }
            if (this.depotNum > 0) {
                this.robot.castleTalk(this.depotNum);
            }
            this.UpdateOccupiedResources();
            if (this.ThreatsAround(this.robot)) {
                this.state = bc19.PilgrimState.Returning;
                this.robot.log("fleeding");
                return this.ReturnToDropOff();
            }
            if (this.state === bc19.PilgrimState.GoingToResource) {
                this.robot.log("gogin");
                return this.GoToMine();
            }
            if (this.state === bc19.PilgrimState.Mining) {
                this.robot.log("minmgng");
                return this.Mining();
            }
            if (this.state === bc19.PilgrimState.Returning) {
                this.robot.log("returbing");
                return this.ReturnToDropOff();
            }
            return null;
        };
        Pilgrim.prototype.InitializeVariables = function () {
            this.karbLocations = ([]);
            this.fuelLocations = ([]);
            this.allRoutes = ({});
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
            var signals = this.ReadPilgrimSignals(this.robot);
            this.depotNum = signals[0];
            if (this.depotNum >= 0) {
                this.churchLocation = bc19.Helper.ChurchLocationsFromClusters(this.robot, bc19.Helper.FindClusters(this.robot, bc19.Helper.ResourcesOnOurHalfMap(this.robot)))[this.depotNum - 1];
            }
            else {
                this.churchLocation = null;
            }
            this.state = bc19.PilgrimState.GoingToResource;
        };
        Pilgrim.prototype.UpdateOccupiedResources = function () {
            for (var i = -this.robot.tileVisionRange; i <= this.robot.tileVisionRange; i++) {
                for (var j = -this.robot.tileVisionRange; j <= this.robot.tileVisionRange; j++) {
                    var yNew = this.robot.me.y + i;
                    var xNew = this.robot.me.x + j;
                    var tile = new bc19.Position(yNew, xNew);
                    if (bc19.Helper.inMap(this.robot.map, tile)) {
                        if (bc19.Helper.DistanceSquared(tile, this.robot.location) > this.robot.visionRange || this.occupiedResources[yNew][xNew] === -1) {
                            continue;
                        }
                        var robots = this.robot.getVisibleRobots();
                        for (var k = 0; k < robots.length; k++) {
                            var robotPos = new bc19.Position(robots[k].y, robots[k].x);
                            if (robots[k].team !== this.robot.ourTeam && bc19.Helper.DistanceSquared(this.robot.location, robotPos) <= this.robot.visionRange && bc19.Helper.DistanceSquared(tile, robotPos) <= this.robot.SPECS.UNITS[robots[k].unit].VISION_RADIUS) {
                                this.occupiedResources[yNew][xNew] = 1;
                            }
                        }
                        ;
                        var robotThere = bc19.Helper.RobotAtPosition(this.robot, tile);
                        if ((robotThere != null)) {
                            this.occupiedResources[yNew][xNew] = 1;
                        }
                        else {
                            this.occupiedResources[yNew][xNew] = 0;
                        }
                    }
                }
                ;
            }
            ;
            if (this.occupiedResources[this.robot.me.y][this.robot.me.x] === 1) {
                this.occupiedResources[this.robot.me.y][this.robot.me.x] = 0;
            }
        };
        Pilgrim.prototype.GetNearestDropOff = function () {
            var robots = this.robot.getVisibleRobots();
            var lowest = Number.MAX_VALUE;
            var closest = null;
            for (var i = 0; i < robots.length; i++) {
                if ((robots[i].unit === this.robot.SPECS.CHURCH || robots[i].unit === this.robot.SPECS.CASTLE) && robots[i].team === this.robot.ourTeam) {
                    var distance = bc19.Helper.DistanceSquared(this.robot.location, new bc19.Position(robots[i].y, robots[i].x));
                    if (distance < lowest) {
                        lowest = distance;
                        closest = new bc19.Position(robots[i].y, robots[i].x);
                    }
                }
            }
            ;
            if (closest != null) {
                return closest;
            }
            return this.spawnLocation;
        };
        Pilgrim.prototype.ReturnToDropOff = function () {
            var dropOff = this.GetNearestDropOff();
            if (bc19.Helper.DistanceSquared(dropOff, this.robot.location) <= 3) {
                this.state = bc19.PilgrimState.GoingToResource;
                this.counter = 0;
                return this.robot.give(dropOff.x - this.robot.me.x, dropOff.y - this.robot.me.y, this.robot.me.karbonite, this.robot.me.fuel);
            }
            if (this.GetOrCreateMap(this.robot, this.allRoutes, dropOff, true)[this.robot.location.y][this.robot.location.x] < 25) {
                if (bc19.Helper.DistanceSquared(this.robot.location, dropOff) <= 16) {
                    this.counter++;
                    if (this.counter > 8) {
                        if (bc19.Helper.Have(this.robot, 50, 250)) {
                            var random = Pilgrim.ChurchBuildPosition(this.robot, this.robot.location);
                            this.counter = 0;
                            return this.robot.buildUnit(this.robot.SPECS.CHURCH, random.x - this.robot.me.x, random.y - this.robot.me.y);
                        }
                    }
                    return bc19.MovingRobot.MoveCloser(this.robot, dropOff, false);
                }
                else {
                    var atkers = this.EnemiesOfTypeInVision(this.robot, [this.robot.SPECS.PREACHER, this.robot.SPECS.PROPHET, this.robot.SPECS.CRUSADER]);
                    var pathingMap = bc19.Helper.twoDimensionalArrayClone(this.GetOrCreateMap(this.robot, this.allRoutes, dropOff, false));
                    if (atkers.length > 0) {
                        pathingMap = this.BlackOutPaths(this.robot, pathingMap, atkers);
                    }
                    return bc19.MovingRobot.FloodPathing(this.robot, pathingMap, dropOff, false);
                }
            }
            return this.Mining();
        };
        Pilgrim.prototype.GetNearestResource = function (start) {
            var chosenLocations = this.depotNum >= 0 ? this.karbLocations : this.fuelLocations;
            var lowest = Number.MAX_VALUE;
            var closest = null;
            for (var i = 0; i < chosenLocations.length; i++) {
                var pos = chosenLocations[i];
                var distance = bc19.Helper.DistanceSquared(pos, start);
                if (this.occupiedResources[pos.y][pos.x] === 0 && distance < lowest) {
                    lowest = distance;
                    closest = pos;
                }
            }
            ;
            chosenLocations = this.depotNum >= 0 ? this.fuelLocations : this.karbLocations;
            for (var i = 0; i < chosenLocations.length; i++) {
                var pos = chosenLocations[i];
                var distance = bc19.Helper.DistanceSquared(pos, start);
                if (this.occupiedResources[pos.y][pos.x] === 0 && distance < Math.fround(lowest - 9)) {
                    lowest = distance;
                    closest = pos;
                }
            }
            ;
            return closest;
        };
        Pilgrim.prototype.GoToMine = function () {
            var nearestInGeneral = this.GetNearestResource(this.robot.location);
            if (this.depotNum >= 0) {
                var nearestToChurch = this.GetNearestResource(this.churchLocation);
                if (bc19.Helper.DistanceSquared(this.robot.location, this.churchLocation) <= 16) {
                    if (this.robot.getKarboniteMap()[this.robot.me.y][this.robot.me.x] || this.robot.getFuelMap()[this.robot.me.y][this.robot.me.x]) {
                        this.state = bc19.PilgrimState.Mining;
                        return this.robot.mine();
                    }
                    else if (bc19.Helper.DistanceSquared(this.churchLocation, nearestToChurch) <= 16) {
                        return bc19.MovingRobot.MoveCloser(this.robot, nearestToChurch, false);
                    }
                    else {
                        var atkers = this.EnemiesOfTypeInVision(this.robot, [this.robot.SPECS.PREACHER, this.robot.SPECS.PROPHET, this.robot.SPECS.CRUSADER]);
                        var pathingMap = bc19.Helper.twoDimensionalArrayClone(this.GetOrCreateMap(this.robot, this.allRoutes, nearestToChurch, false));
                        if (atkers.length > 0) {
                            pathingMap = this.BlackOutPaths(this.robot, pathingMap, atkers);
                        }
                        return bc19.MovingRobot.FloodPathing(this.robot, pathingMap, nearestInGeneral, true);
                    }
                }
                else {
                    var atkers = this.EnemiesOfTypeInVision(this.robot, [this.robot.SPECS.PREACHER, this.robot.SPECS.PROPHET, this.robot.SPECS.CRUSADER]);
                    var pathingMap = bc19.Helper.twoDimensionalArrayClone(this.GetOrCreateMap(this.robot, this.allRoutes, this.churchLocation, false));
                    if (atkers.length > 0) {
                        pathingMap = this.BlackOutPaths(this.robot, pathingMap, atkers);
                    }
                    return bc19.MovingRobot.FloodPathing(this.robot, pathingMap, this.churchLocation, false);
                }
            }
            else {
                if (this.robot.getKarboniteMap()[this.robot.me.y][this.robot.me.x] || this.robot.getFuelMap()[this.robot.me.y][this.robot.me.x]) {
                    this.state = bc19.PilgrimState.Mining;
                    return this.robot.mine();
                }
                else {
                    var atkers = this.EnemiesOfTypeInVision(this.robot, [this.robot.SPECS.PREACHER, this.robot.SPECS.PROPHET, this.robot.SPECS.CRUSADER]);
                    var pathingMap = bc19.Helper.twoDimensionalArrayClone(this.GetOrCreateMap(this.robot, this.allRoutes, nearestInGeneral, false));
                    if (atkers.length > 0) {
                        pathingMap = this.BlackOutPaths(this.robot, pathingMap, atkers);
                    }
                    return bc19.MovingRobot.FloodPathing(this.robot, pathingMap, nearestInGeneral, false);
                }
            }
        };
        Pilgrim.prototype.Mining = function () {
            this.robot.log("here1");
            var numChurchs = bc19.StationairyRobot.UnitAround(this.robot, this.robot.location, 6, this.robot.SPECS.CHURCH);
            var numPilgrims = bc19.StationairyRobot.UnitAround(this.robot, this.robot.location, 6, this.robot.SPECS.PILGRIM);
            if (((((numChurchs * 8) / numPilgrims | 0)) | 0) === 0 && bc19.Helper.Have(this.robot, 50, 250) && bc19.StationairyRobot.UnitAround(this.robot, this.robot.location, 6, this.robot.SPECS.CASTLE) === 0 && bc19.Helper.DistanceSquared(this.robot.location, this.GetNearestDropOff()) >= 9) {
                this.robot.log("here22");
                var random = Pilgrim.ChurchBuildPosition(this.robot, this.robot.location);
                this.robot.log("Position " + (random == null));
                return this.robot.buildUnit(this.robot.SPECS.CHURCH, random.x - this.robot.me.x, random.y - this.robot.me.y);
            }
            if (this.robot.me.karbonite >= this.robot.karbCapacity || this.robot.me.fuel >= this.robot.fuelCapacity) {
                this.robot.log("here3");
                this.state = bc19.PilgrimState.Returning;
            }
            else {
                this.robot.log("here4");
                return this.robot.mine();
            }
            return null;
        };
        Pilgrim.prototype.ImTheClosestPilgrim = function (pos) {
            var robots = this.robot.getVisibleRobots();
            var myDist = bc19.Helper.DistanceSquared(this.robot.location, pos);
            for (var i = 0; i < robots.length; i++) {
                var pil = new bc19.Position(robots[i].y, robots[i].x);
                if (bc19.Helper.DistanceSquared(pos, pil) >= 49) {
                    continue;
                }
                if (robots[i].unit === this.robot.SPECS.PILGRIM && !this.robot.getFuelMap()[pil.y][pil.x] && !this.robot.getKarboniteMap()[pil.y][pil.x] && bc19.Helper.DistanceSquared(pos, pil) < myDist) {
                    return false;
                }
            }
            ;
            return true;
        };
        Pilgrim.ChurchBuildPosition = function (robot, pos) {
            robot.log("1");
            var highest = -100;
            var best = null;
            for (var i = -1; i <= 1; i++) {
                for (var j = -1; j <= 1; j++) {
                    robot.log("2");
                    var adjacent = new bc19.Position(pos.y + i, pos.x + j);
                    robot.log("adj" + adjacent);
                    if (bc19.Helper.inMap(robot.map, adjacent) && bc19.Helper.TileEmptyNonResource(robot, adjacent)) {
                        var count = 0;
                        for (var k = -1; k <= 1; k++) {
                            for (var l = -1; l <= 1; l++) {
                                var surround = new bc19.Position(adjacent.y + k, adjacent.x + l);
                                if (bc19.Helper.inMap(robot.map, surround) && (robot.getKarboniteMap()[surround.y][surround.x] || robot.getFuelMap()[surround.y][surround.x])) {
                                    count++;
                                }
                            }
                            ;
                        }
                        ;
                        robot.log("3");
                        if (count > highest) {
                            highest = count;
                            best = adjacent;
                        }
                    }
                }
                ;
            }
            ;
            return best;
        };
        return Pilgrim;
    }(bc19.MovingRobot));
    bc19.Pilgrim = Pilgrim;
    Pilgrim["__class"] = "bc19.Pilgrim";
    Pilgrim["__interfaces"] = ["bc19.Machine"];
    var PilgrimState;
    (function (PilgrimState) {
        PilgrimState[PilgrimState["GoingToResource"] = 0] = "GoingToResource";
        PilgrimState[PilgrimState["Mining"] = 1] = "Mining";
        PilgrimState[PilgrimState["Returning"] = 2] = "Returning";
    })(PilgrimState = bc19.PilgrimState || (bc19.PilgrimState = {}));
})(bc19 || (bc19 = {}));
(function (bc19) {
    var Preacher = (function (_super) {
        __extends(Preacher, _super);
        function Preacher(robot) {
            var _this = _super.call(this) || this;
            _this.robot = null;
            _this.initialized = false;
            _this.parent = null;
            _this.parentLocation = null;
            _this.targetCastle = null;
            _this.castleLocations = null;
            _this.enemyCastleLocations = null;
            _this.routesToEnemies = null;
            _this.defensePosition = null;
            _this.manualFort = false;
            _this.fortCount = 0;
            _this.robot = robot;
            return _this;
        }
        Preacher.prototype.Execute = function () {
            this.robot.log("Preacher : " + this.robot.location);
            if (this.robot.me.turn === 1) {
                this.InitializeVariables();
                this.parent = this.StructureBornFrom(this.robot);
                this.parentLocation = new bc19.Position(this.parent.y, this.parent.x);
                if (this.parent.unit === this.robot.SPECS.CHURCH) {
                    this.initialized = true;
                }
            }
            if (!this.initialized) {
                this.CastleInit();
            }
            this.targetCastle = bc19.MovingRobot.UpdateBattleStatus(this.robot, this.enemyCastleLocations, this.targetCastle);
            var invader = this.ListenForDefense(this.robot);
            if (bc19.Helper.Have(this.robot, 0, 50)) {
                if (bc19.Helper.EnemiesAround(this.robot)) {
                    return this.AttackEnemies();
                }
                else if (invader != null) {
                    if (this.defensePosition == null) {
                        var defensePositions = this.GetValidDefense(this.robot, this.routesToEnemies, this.parentLocation, invader);
                        this.defensePosition = bc19.Helper.ClosestPosition(this.robot, defensePositions);
                    }
                    if (!this.defensePosition.equals(this.robot.location)) {
                        return bc19.MovingRobot.MoveCloser(this.robot, this.defensePosition, false);
                    }
                }
            }
            if (this.initialized && bc19.Helper.Have(this.robot, 0, 325)) {
                if (this.targetCastle == null && !this.Fortified(this.robot, this.robot.location) && !this.manualFort) {
                    this.fortCount++;
                    if (this.fortCount > 10) {
                        this.manualFort = true;
                    }
                    var valid = this.GetValidFortifiedPositions(this.robot, this.parentLocation);
                    if (valid.length > 0) {
                        var closest = bc19.Helper.ClosestPosition(this.robot, valid);
                        var shortPath = bc19.MovingRobot.CreateLayeredFloodPath$bc19_MyRobot$bc19_Position$bc19_Position(this.robot, closest, this.robot.location);
                        return bc19.MovingRobot.FloodPathing(this.robot, shortPath, closest, false);
                    }
                    else {
                        var goal = null;
                        if (this.robot.mapIsHorizontal) {
                            if (this.robot.me.y > (this.robot.map.length / 2 | 0)) {
                                goal = new bc19.Position(0, this.robot.me.x);
                            }
                            else {
                                goal = new bc19.Position(this.robot.map.length - 1, this.robot.me.x);
                            }
                        }
                        else {
                            if (this.robot.me.x > (this.robot.map.length / 2 | 0)) {
                                goal = new bc19.Position(this.robot.me.y, 0);
                            }
                            else {
                                goal = new bc19.Position(this.robot.me.y, this.robot.map.length - 1);
                            }
                        }
                        return bc19.MovingRobot.FloodPathing(this.robot, this.GetOrCreateMap(this.robot, this.routesToEnemies, goal, false), goal, false);
                    }
                }
                else if (this.targetCastle != null) {
                    var rushTime = true;
                    this.CastleDown(this.robot, this.enemyCastleLocations, this.routesToEnemies);
                    if (bc19.Helper.ContainsPosition(this.enemyCastleLocations, this.targetCastle)) {
                        if (bc19.Helper.DistanceSquared(this.robot.location, this.targetCastle) <= 196) {
                            rushTime = false;
                        }
                        return bc19.MovingRobot.FloodPathing(this.robot, this.GetOrCreateMap(this.robot, this.routesToEnemies, this.targetCastle, true), this.targetCastle, rushTime);
                    }
                    else {
                        var closestEnemyCastle = this.ClosestEnemyCastle(this.robot, this.routesToEnemies);
                        if (closestEnemyCastle != null && bc19.Helper.DistanceSquared(this.robot.location, closestEnemyCastle) <= 196) {
                            rushTime = false;
                        }
                        return bc19.MovingRobot.FloodPathing(this.robot, this.GetOrCreateMap(this.robot, this.routesToEnemies, closestEnemyCastle, true), closestEnemyCastle, rushTime);
                    }
                }
            }
            return null;
        };
        Preacher.prototype.CastleInit = function () {
            this.initialized = this.ReadCombatSignals(this.robot, this.castleLocations);
            if (this.initialized) {
                this.enemyCastleLocations = bc19.Helper.FindEnemyCastles(this.robot, this.robot.mapIsHorizontal, this.castleLocations);
                for (var i = 0; i < this.enemyCastleLocations.length; i++) {
                    this.GetOrCreateMap(this.robot, this.routesToEnemies, /* get */ this.enemyCastleLocations[i], false);
                }
                ;
            }
        };
        Preacher.prototype.InitializeVariables = function () {
            this.castleLocations = ([]);
            this.enemyCastleLocations = ([]);
            this.routesToEnemies = ({});
        };
        Preacher.prototype.AttackEnemies = function () {
            var most = Number.MIN_VALUE;
            var attackTile = null;
            for (var i = -this.robot.tileVisionRange; i <= this.robot.tileVisionRange; i++) {
                for (var j = -this.robot.tileVisionRange; j <= this.robot.tileVisionRange; j++) {
                    var checkTile = new bc19.Position(this.robot.me.y + i, this.robot.me.x + j);
                    if (bc19.Helper.inMap(this.robot.map, checkTile) && bc19.Helper.DistanceSquared(checkTile, this.robot.location) <= this.robot.visionRange) {
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
            return this.robot.attack(attackTile.x - this.robot.me.x, attackTile.y - this.robot.me.y);
        };
        Preacher.prototype.NumAdjacentEnemies = function (pos) {
            var numEnemies = 0;
            var robots = this.robot.getVisibleRobots();
            for (var i = -1; i <= 1; i++) {
                for (var j = -1; j <= 1; j++) {
                    for (var k = 0; k < robots.length; k++) {
                        if (pos.y + i === robots[k].y && pos.x + j === robots[k].x) {
                            if (robots[k].team === this.robot.ourTeam && robots[k].unit === this.robot.SPECS.CASTLE) {
                                numEnemies -= 8;
                            }
                            else if (robots[k].team === this.robot.ourTeam) {
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
            _this.parent = null;
            _this.parentLocation = null;
            _this.targetCastle = null;
            _this.castleLocations = null;
            _this.enemyCastleLocations = null;
            _this.routesToEnemies = null;
            _this.manualFort = false;
            _this.fortCount = 0;
            _this.robot = robot;
            return _this;
        }
        Prophet.prototype.Execute = function () {
            this.robot.log("Prophet : " + this.robot.location);
            if (this.robot.me.turn === 1) {
                this.InitializeVariables();
                this.parent = this.StructureBornFrom(this.robot);
                this.parentLocation = new bc19.Position(this.parent.y, this.parent.x);
                if (this.parent.unit === this.robot.SPECS.CHURCH) {
                    this.initialized = true;
                }
            }
            if (!this.initialized) {
                this.CastleInit();
            }
            this.targetCastle = bc19.MovingRobot.UpdateBattleStatus(this.robot, this.enemyCastleLocations, this.targetCastle);
            if (bc19.Helper.EnemiesAround(this.robot)) {
                var closeEnemies = bc19.Helper.EnemiesWithin(this.robot, this.robot.attackRange[0]);
                if (this.initialized && closeEnemies.length > 0 && bc19.Helper.Have(this.robot, 0, 50)) {
                    return this.Flee(closeEnemies);
                }
                else if (this.robot.fuel > 110) {
                    var attackable = bc19.Helper.EnemiesWithin(this.robot, this.robot.attackRange[1]);
                    return this.AttackEnemies(/* toArray */ attackable.slice(0));
                }
            }
            if (this.initialized && bc19.Helper.Have(this.robot, 0, 325)) {
                if (this.targetCastle == null && !this.Fortified(this.robot, this.robot.location) && !this.manualFort) {
                    this.fortCount++;
                    if (this.fortCount > 10) {
                        this.manualFort = true;
                    }
                    var valid = this.GetValidFortifiedPositions(this.robot, this.parentLocation);
                    if (valid.length > 0) {
                        var closest = bc19.Helper.ClosestPosition(this.robot, valid);
                        var shortPath = bc19.MovingRobot.CreateLayeredFloodPath$bc19_MyRobot$bc19_Position$bc19_Position(this.robot, closest, this.robot.location);
                        return bc19.MovingRobot.FloodPathing(this.robot, shortPath, closest, false);
                    }
                    else {
                        var goal = null;
                        if (this.robot.mapIsHorizontal) {
                            if (this.robot.me.y > (this.robot.map.length / 2 | 0)) {
                                goal = new bc19.Position(0, this.robot.me.x);
                            }
                            else {
                                goal = new bc19.Position(this.robot.map.length - 1, this.robot.me.x);
                            }
                        }
                        else {
                            if (this.robot.me.x > (this.robot.map.length / 2 | 0)) {
                                goal = new bc19.Position(this.robot.me.y, 0);
                            }
                            else {
                                goal = new bc19.Position(this.robot.me.y, this.robot.map.length - 1);
                            }
                        }
                        return bc19.MovingRobot.FloodPathing(this.robot, this.GetOrCreateMap(this.robot, this.routesToEnemies, goal, false), goal, false);
                    }
                }
                else if (this.targetCastle != null) {
                    this.CastleDown(this.robot, this.enemyCastleLocations, this.routesToEnemies);
                    if (bc19.Helper.ContainsPosition(this.enemyCastleLocations, this.targetCastle)) {
                        return bc19.MovingRobot.FloodPathing(this.robot, this.GetOrCreateMap(this.robot, this.routesToEnemies, this.targetCastle, true), this.targetCastle, true);
                    }
                    else {
                        var closestEnemyCastle = this.ClosestEnemyCastle(this.robot, this.routesToEnemies);
                        return bc19.MovingRobot.FloodPathing(this.robot, this.GetOrCreateMap(this.robot, this.routesToEnemies, closestEnemyCastle, true), closestEnemyCastle, true);
                    }
                }
            }
            return null;
        };
        Prophet.prototype.CastleInit = function () {
            this.initialized = this.ReadCombatSignals(this.robot, this.castleLocations);
            if (this.initialized) {
                this.enemyCastleLocations = bc19.Helper.FindEnemyCastles(this.robot, this.robot.mapIsHorizontal, this.castleLocations);
                for (var i = 0; i < this.enemyCastleLocations.length; i++) {
                    this.GetOrCreateMap(this.robot, this.routesToEnemies, /* get */ this.enemyCastleLocations[i], false);
                }
                ;
            }
        };
        Prophet.prototype.InitializeVariables = function () {
            this.castleLocations = ([]);
            this.enemyCastleLocations = ([]);
            this.routesToEnemies = ({});
        };
        Prophet.prototype.AttackEnemies = function (robots) {
            var attackTile = null;
            var lowestID = Number.MAX_VALUE;
            for (var i = 0; i < robots.length; i++) {
                var robotPos = new bc19.Position(robots[i].y, robots[i].x);
                if (robots[i].team !== this.robot.ourTeam && bc19.Helper.DistanceSquared(robotPos, this.robot.location) <= this.robot.attackRange[1]) {
                    if (robots[i].id < lowestID) {
                        lowestID = robots[i].id;
                        attackTile = robotPos;
                    }
                }
            }
            ;
            return this.robot.attack(attackTile.x - this.robot.me.x, attackTile.y - this.robot.me.y);
        };
        Prophet.prototype.Flee = function (robots) {
            var closest = bc19.Helper.closestEnemy(this.robot, robots);
            var dx = closest.x - this.robot.me.x;
            var dy = closest.y - this.robot.me.y;
            var opposite = new bc19.Position(this.robot.me.y - dy, this.robot.me.x - dx);
            return bc19.MovingRobot.MoveCloser(this.robot, opposite, false);
        };
        Prophet.prototype.GetMyCastlePosition = function () {
            var robots = this.robot.getVisibleRobots();
            var closest = Number.MAX_VALUE;
            var myCastle = null;
            for (var i = 0; i < robots.length; i++) {
                var rp = new bc19.Position(robots[i].y, robots[i].x);
                if (robots[i].unit === this.robot.SPECS.CASTLE && bc19.Helper.DistanceSquared(rp, this.robot.location) < closest) {
                    closest = bc19.Helper.DistanceSquared(rp, this.robot.location);
                    myCastle = rp;
                }
            }
            ;
            return myCastle;
        };
        return Prophet;
    }(bc19.MovingRobot));
    bc19.Prophet = Prophet;
    Prophet["__class"] = "bc19.Prophet";
    Prophet["__interfaces"] = ["bc19.Machine"];
})(bc19 || (bc19 = {}));
(function (bc19) {
    var Castle = (function (_super) {
        __extends(Castle, _super);
        function Castle(robot) {
            var _this = _super.call(this) || this;
            _this.initialized = false;
            _this.positionInSpawnOrder = 0;
            _this.targetCastleIndex = 0;
            _this.robot = null;
            _this.numCastles = 0;
            _this.allyCastlePositions = null;
            _this.allyCastles = null;
            _this.churchLocations = null;
            _this.resourceDepots = null;
            _this.depotNum = 0;
            _this.starterPilgrims = 0;
            _this.idDone = 0;
            _this.spawnOrder = null;
            _this.robot = robot;
            return _this;
        }
        Castle.prototype.Execute = function () {
            this.robot.log("Castle : " + this.robot.location + " Turn : " + this.robot.me.turn);
            var output = null;
            var signal = -1;
            var signalRadius = 0;
            if (!this.initialized) {
                this.Initialize();
            }
            this.positionInSpawnOrder = this.positionInSpawnOrder === this.spawnOrder.length ? 0 : this.positionInSpawnOrder;
            if (this.initialized && bc19.Helper.Have(this.robot, 50 + this.robot.SPECS.UNITS[this.spawnOrder[this.positionInSpawnOrder]].CONSTRUCTION_KARBONITE, 500)) {
                var buildHere = bc19.Helper.RandomAdjacentNonResource(this.robot, this.robot.location);
                if (buildHere != null) {
                    signal = this.DeclareAllyCastlePositions(1);
                    output = this.robot.buildUnit(this.spawnOrder[this.positionInSpawnOrder], buildHere.x - this.robot.me.x, buildHere.y - this.robot.me.y);
                    this.positionInSpawnOrder++;
                }
            }
            var resources = this.ResourcesAround(this.robot, 3);
            var pilgrims = this.CastlePilgrims();
            if (resources > pilgrims) {
                var buildHere = bc19.Helper.RandomAdjacentNonResource(this.robot, this.robot.location);
                if (buildHere != null && bc19.Helper.Have(this.robot, 20, 125) || this.starterPilgrims < 4) {
                    signal = this.depotNum;
                    this.starterPilgrims++;
                    output = this.robot.buildUnit(this.robot.SPECS.PILGRIM, buildHere.x - this.robot.me.x, buildHere.y - this.robot.me.y);
                }
            }
            else if (bc19.Helper.Have(this.robot, 20, 125) || this.starterPilgrims < 4) {
                this.UpdateDepots();
                var pilgrimPosition = this.ShouldBuildPilgrim();
                if (pilgrimPosition != null) {
                    var random = bc19.Helper.RandomAdjacent(this.robot, new bc19.Position(this.robot.me.y, this.robot.me.x));
                    this.starterPilgrims++;
                    signal = this.SignalToPilgrim(pilgrimPosition);
                    output = this.robot.buildUnit(this.robot.SPECS.PILGRIM, random.x - this.robot.me.x, random.y - this.robot.me.y);
                }
            }
            if (this.NumPilgrims() < 2) {
                this.UpdateDepots();
                var pilgrimPosition = this.ShouldBuildPilgrim();
                if (pilgrimPosition != null) {
                    var random = bc19.Helper.RandomAdjacent(this.robot, new bc19.Position(this.robot.me.y, this.robot.me.x));
                    signal = this.SignalToPilgrim(pilgrimPosition);
                    output = this.robot.buildUnit(this.robot.SPECS.PILGRIM, random.x - this.robot.me.x, random.y - this.robot.me.y);
                }
            }
            if (bc19.Helper.EnemiesAround(this.robot)) {
                var canBuildDefense = this.EvaluateEnemyRatio(this.robot);
                if (canBuildDefense != null) {
                    signal = this.CreateAttackSignal(bc19.Helper.closestEnemy(this.robot, bc19.Helper.EnemiesWithin(this.robot, this.robot.visionRange)), 8);
                    output = canBuildDefense;
                }
                else {
                    var enemiesAttacking = bc19.Helper.EnemiesWithin(this.robot, this.robot.attackRange[1]);
                    var closestEnemy = bc19.Helper.closestEnemy(this.robot, enemiesAttacking);
                    if (closestEnemy != null) {
                        output = this.robot.attack(closestEnemy.x - this.robot.me.x, closestEnemy.y - this.robot.me.y);
                    }
                }
            }
            var atkSignal = this.SignalAttack();
            signal = atkSignal === -1 ? signal : atkSignal;
            signalRadius = atkSignal === -1 ? 3 : this.robot.map.length * this.robot.map.length + this.robot.map.length * this.robot.map.length;
            if (this.robot.getVisibleRobots().length === this.numCastles) {
                var buildHere = bc19.Helper.RandomAdjacentNonResource(this.robot, this.robot.location);
                if (buildHere != null) {
                    this.UpdateDepots();
                    signal = this.SignalToPilgrim(this.DepotClosestToCenter());
                    this.starterPilgrims++;
                    output = this.robot.buildUnit(this.robot.SPECS.PILGRIM, buildHere.x - this.robot.me.x, buildHere.y - this.robot.me.y);
                }
            }
            if (this.robot.me.turn === 1 && this.numCastles === 1) {
                signal = this.CreateAttackSignal(bc19.Helper.FindEnemyCastle(this.robot.map, this.robot.mapIsHorizontal, this.robot.location), 8);
                var random = bc19.StationairyRobot.RandomAdjacentTowardsEnemy(this.robot, bc19.Helper.FindEnemyCastle(this.robot.map, this.robot.mapIsHorizontal, this.robot.location));
                output = this.robot.buildUnit(this.robot.SPECS.PREACHER, random.x - this.robot.me.x, random.y - this.robot.me.y);
            }
            if (signal > -1) {
                this.robot.signal(signal, signalRadius);
            }
            return output;
        };
        Castle.prototype.Initialize = function () {
            if (this.robot.me.turn === 1) {
                this.InitializeVariables();
            }
            if (!this.initialized) {
                this.initialized = this.SetupAllyCastles();
            }
            if (this.initialized) {
                {
                    var array128 = (function (m) { var r = []; if (m.entries == null)
                        m.entries = []; for (var i = 0; i < m.entries.length; i++)
                        r.push(m.entries[i].value); return r; })(this.allyCastles);
                    for (var index127 = 0; index127 < array128.length; index127++) {
                        var pos = array128[index127];
                        {
                            /* add */ (this.allyCastlePositions.push(pos) > 0);
                        }
                    }
                }
            }
        };
        Castle.prototype.InitializeVariables = function () {
            this.spawnOrder = [this.robot.SPECS.PROPHET, this.robot.SPECS.CRUSADER, this.robot.SPECS.CRUSADER, this.robot.SPECS.CRUSADER, this.robot.SPECS.PREACHER];
            this.positionInSpawnOrder = 0;
            this.allyCastles = ({});
            this.allyCastlePositions = ([]);
            var temp = bc19.Helper.FindClusters(this.robot, bc19.Helper.ResourcesOnOurHalfMap(this.robot));
            this.churchLocations = bc19.Helper.ChurchLocationsFromClusters(this.robot, temp);
            this.resourceDepots = (function (s) { var a = []; while (s-- > 0)
                a.push(0); return a; })(/* size */ temp.length);
            var closestDepot = bc19.Helper.ClosestPosition(this.robot, this.churchLocations);
            this.depotNum = this.churchLocations.indexOf(closestDepot) + 1;
            this.targetCastleIndex = 0;
        };
        Castle.prototype.NumPilgrims = function () {
            var robots = this.robot.getVisibleRobots();
            var count = 0;
            for (var i = 0; i < robots.length; i++) {
                if (robots[i].castle_talk > 0 && robots[i].castle_talk <= 31) {
                    count++;
                }
            }
            ;
            return count;
        };
        Castle.prototype.UpdateDepots = function () {
            for (var i = 0; i < this.resourceDepots.length; i++) {
                this.resourceDepots[i] = -(i + 1);
                var church = this.churchLocations[i];
                for (var j = 0; j < this.allyCastlePositions.length; j++) {
                    var otherCastle = this.allyCastlePositions[j];
                    if (!otherCastle.equals(this.robot.location) && bc19.Helper.DistanceSquared(otherCastle, church) < bc19.Helper.DistanceSquared(this.robot.location, church)) {
                        this.resourceDepots[i] = (i + 1);
                    }
                }
                ;
            }
            ;
            var robots = this.robot.getVisibleRobots();
            for (var i = 0; i < robots.length; i++) {
                var signal = robots[i].castle_talk;
                if (signal <= this.resourceDepots.length) {
                    this.resourceDepots[signal - 1] = signal;
                }
            }
            ;
        };
        Castle.prototype.SignalAttack = function () {
            var otherCastlesCry = bc19.MovingRobot.ListenForBattleCry(this.robot);
            this.targetCastleIndex = this.targetCastleIndex >= this.numCastles ? 0 : this.targetCastleIndex;
            if (otherCastlesCry == null && this.robot.me.turn % 300 === 0 && bc19.Helper.Have(this.robot, 0, 80 * this.robot.map.length)) {
                var enemyCastle = bc19.Helper.FindEnemyCastle(this.robot.map, this.robot.mapIsHorizontal, /* get */ this.allyCastlePositions[this.targetCastleIndex]);
                this.targetCastleIndex++;
                return this.CreateAttackSignal(enemyCastle, this.robot.me.turn === 900 ? 4 : 2);
            }
            return -1;
        };
        Castle.prototype.ShouldBuildPilgrim = function () {
            var available = ([]);
            for (var i = 0; i < this.churchLocations.length; i++) {
                if (this.resourceDepots[i] < 0) {
                    /* add */ (available.push(/* get */ this.churchLocations[i]) > 0);
                }
            }
            ;
            return bc19.Helper.ClosestPosition(this.robot, available);
        };
        Castle.prototype.DepotClosestToCenter = function () {
            var lowest = 128;
            var closest = null;
            for (var i = 0; i < this.churchLocations.length; i++) {
                if (this.resourceDepots[i] < 0) {
                    var current = Number.MAX_VALUE;
                    if (this.robot.mapIsHorizontal) {
                        current = bc19.Helper.abs(/* get */ this.churchLocations[i].y - (((this.robot.map.length + 1) / 2 | 0)));
                    }
                    else {
                        current = bc19.Helper.abs(/* get */ this.churchLocations[i].x - (((this.robot.map.length + 1) / 2 | 0)));
                    }
                    if (current < lowest) {
                        lowest = current;
                        closest = this.churchLocations[i];
                    }
                }
            }
            ;
            return closest;
        };
        Castle.prototype.SignalToPilgrim = function (pos) {
            var depotNum = this.churchLocations.indexOf(pos) + 1;
            return depotNum;
        };
        Castle.prototype.CastlePilgrims = function () {
            var robots = this.robot.getVisibleRobots();
            var count = 0;
            for (var i = 0; i < robots.length; i++) {
                if (robots[i].castle_talk === this.depotNum) {
                    count++;
                }
            }
            ;
            return count;
        };
        Castle.prototype.SetupAllyCastles = function () {
            var rs = this.robot.getVisibleRobots();
            var robots = ([]);
            for (var i = 0; i < rs.length; i++) {
                if (rs[i].team === this.robot.ourTeam) {
                    /* add */ (robots.push(rs[i]) > 0);
                }
            }
            ;
            if (robots.length === 1) {
                this.numCastles = 1;
                /* put */ (function (m, k, v) { if (m.entries == null)
                    m.entries = []; for (var i = 0; i < m.entries.length; i++)
                    if (m.entries[i].key.equals != null && m.entries[i].key.equals(k) || m.entries[i].key === k) {
                        m.entries[i].value = v;
                        return;
                    } m.entries.push({ key: k, value: v, getKey: function () { return this.key; }, getValue: function () { return this.value; } }); })(this.allyCastles, this.robot.id, this.robot.location);
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
                    if (robots[i].castle_talk > 31 && robots[i].id !== this.robot.id) {
                        var info = new bc19.CastleLocation(/* get */ robots[i].castle_talk);
                        this.numCastles = info.threeCastles ? 3 : 2;
                        if ((function (m, k) { if (m.entries == null)
                            m.entries = []; for (var i_2 = 0; i_2 < m.entries.length; i_2++)
                            if (m.entries[i_2].key.equals != null && m.entries[i_2].key.equals(k) || m.entries[i_2].key === k) {
                                return true;
                            } return false; })(this.allyCastles, /* get */ robots[i].id)) {
                            var current = (function (m, k) { if (m.entries == null)
                                m.entries = []; for (var i_3 = 0; i_3 < m.entries.length; i_3++)
                                if (m.entries[i_3].key.equals != null && m.entries[i_3].key.equals(k) || m.entries[i_3].key === k) {
                                    return m.entries[i_3].value;
                                } return null; })(this.allyCastles, /* get */ robots[i].id);
                            var newY = current.y === -1 ? info.location : current.y;
                            var newX = current.x === -1 ? info.location : current.x;
                            var input = new bc19.Position(newY, newX);
                            /* put */ (function (m, k, v) { if (m.entries == null)
                                m.entries = []; for (var i_4 = 0; i_4 < m.entries.length; i_4++)
                                if (m.entries[i_4].key.equals != null && m.entries[i_4].key.equals(k) || m.entries[i_4].key === k) {
                                    m.entries[i_4].value = v;
                                    return;
                                } m.entries.push({ key: k, value: v, getKey: function () { return this.key; }, getValue: function () { return this.value; } }); })(this.allyCastles, /* get */ robots[i].id, input);
                        }
                        else {
                            var input = new bc19.Position(info.location, -1);
                            /* put */ (function (m, k, v) { if (m.entries == null)
                                m.entries = []; for (var i_5 = 0; i_5 < m.entries.length; i_5++)
                                if (m.entries[i_5].key.equals != null && m.entries[i_5].key.equals(k) || m.entries[i_5].key === k) {
                                    m.entries[i_5].value = v;
                                    return;
                                } m.entries.push({ key: k, value: v, getKey: function () { return this.key; }, getValue: function () { return this.value; } }); })(this.allyCastles, /* get */ robots[i].id, input);
                        }
                    }
                }
                ;
            }
            if ((function (m, k) { if (m.entries == null)
                m.entries = []; for (var i = 0; i < m.entries.length; i++)
                if (m.entries[i].key.equals != null && m.entries[i].key.equals(k) || m.entries[i].key === k) {
                    return true;
                } return false; })(this.allyCastles, this.robot.me.id)) {
                this.robot.castleTalk(this.CastleInfoTalk(this.numCastles === 3 ? true : false, true, this.robot.me.x));
            }
            else {
                /* put */ (function (m, k, v) { if (m.entries == null)
                    m.entries = []; for (var i = 0; i < m.entries.length; i++)
                    if (m.entries[i].key.equals != null && m.entries[i].key.equals(k) || m.entries[i].key === k) {
                        m.entries[i].value = v;
                        return;
                    } m.entries.push({ key: k, value: v, getKey: function () { return this.key; }, getValue: function () { return this.value; } }); })(this.allyCastles, this.robot.me.id, this.robot.location);
                this.robot.castleTalk(this.CastleInfoTalk(this.numCastles === 3 ? true : false, true, this.robot.me.y));
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
                m.entries = []; return m.entries.length; })(this.allyCastles) < this.numCastles) {
                return false;
            }
            {
                var array130 = (function (m) { var r = []; if (m.entries == null)
                    m.entries = []; for (var i = 0; i < m.entries.length; i++)
                    r.push(m.entries[i].value); return r; })(this.allyCastles);
                for (var index129 = 0; index129 < array130.length; index129++) {
                    var pos = array130[index129];
                    {
                        if (pos.y === -1 || pos.x === -1) {
                            return false;
                        }
                    }
                }
            }
            return true;
        };
        Castle.prototype.DeclareAllyCastlePositions = function (message) {
            if (this.numCastles === 1) {
                return this.BinarySignalsForInitialization(message, this.robot.location);
            }
            else if (this.numCastles === 2) {
                var other = null;
                {
                    var array132 = (function (m) { var r = []; if (m.entries == null)
                        m.entries = []; for (var i = 0; i < m.entries.length; i++)
                        r.push(m.entries[i].key); return r; })(this.allyCastles);
                    for (var index131 = 0; index131 < array132.length; index131++) {
                        var id = array132[index131];
                        {
                            if (id !== this.robot.id) {
                                other = (function (m, k) { if (m.entries == null)
                                    m.entries = []; for (var i = 0; i < m.entries.length; i++)
                                    if (m.entries[i].key.equals != null && m.entries[i].key.equals(k) || m.entries[i].key === k) {
                                        return m.entries[i].value;
                                    } return null; })(this.allyCastles, id);
                            }
                        }
                    }
                }
                if (other != null && other.x >= 0 && other.y >= 0) {
                    return this.BinarySignalsForInitialization(message, other);
                }
            }
            else if (this.numCastles === 3) {
                if (this.idDone === 0) {
                    var other = null;
                    {
                        var array134 = (function (m) { var r = []; if (m.entries == null)
                            m.entries = []; for (var i = 0; i < m.entries.length; i++)
                            r.push(m.entries[i].key); return r; })(this.allyCastles);
                        for (var index133 = 0; index133 < array134.length; index133++) {
                            var id = array134[index133];
                            {
                                if (id !== this.robot.id) {
                                    this.idDone = id;
                                    other = (function (m, k) { if (m.entries == null)
                                        m.entries = []; for (var i = 0; i < m.entries.length; i++)
                                        if (m.entries[i].key.equals != null && m.entries[i].key.equals(k) || m.entries[i].key === k) {
                                            return m.entries[i].value;
                                        } return null; })(this.allyCastles, id);
                                }
                            }
                        }
                    }
                    if (other != null && other.x >= 0 && other.y >= 0) {
                        return this.BinarySignalsForInitialization(message, other);
                    }
                }
                else {
                    var other = null;
                    {
                        var array136 = (function (m) { var r = []; if (m.entries == null)
                            m.entries = []; for (var i = 0; i < m.entries.length; i++)
                            r.push(m.entries[i].key); return r; })(this.allyCastles);
                        for (var index135 = 0; index135 < array136.length; index135++) {
                            var id = array136[index135];
                            {
                                if (id !== this.robot.id && id !== this.idDone) {
                                    this.idDone = 0;
                                    other = (function (m, k) { if (m.entries == null)
                                        m.entries = []; for (var i = 0; i < m.entries.length; i++)
                                        if (m.entries[i].key.equals != null && m.entries[i].key.equals(k) || m.entries[i].key === k) {
                                            return m.entries[i].value;
                                        } return null; })(this.allyCastles, id);
                                }
                            }
                        }
                    }
                    if (other != null && other.x >= 0 && other.y >= 0) {
                        return this.BinarySignalsForInitialization(message, other);
                    }
                }
            }
            return -1;
        };
        Castle.prototype.BinarySignalsForInitialization = function (message, pos) {
            var output = message;
            output <<= 6;
            output += pos.y;
            output <<= 6;
            output += pos.x;
            return output;
        };
        return Castle;
    }(bc19.StationairyRobot));
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
    var Church = (function (_super) {
        __extends(Church, _super);
        function Church(robot) {
            var _this = _super.call(this) || this;
            _this.positionInSpawnOrder = 0;
            _this.robot = null;
            _this.spawnOrder = null;
            _this.robot = robot;
            return _this;
        }
        Church.prototype.Execute = function () {
            this.robot.log("Church : " + this.robot.location);
            if (this.robot.me.turn === 1) {
                this.spawnOrder = [this.robot.SPECS.PROPHET, this.robot.SPECS.CRUSADER, this.robot.SPECS.CRUSADER, this.robot.SPECS.CRUSADER, this.robot.SPECS.PREACHER];
            }
            var output = null;
            var signal = -1;
            this.positionInSpawnOrder = this.positionInSpawnOrder === this.spawnOrder.length ? 0 : this.positionInSpawnOrder;
            if (bc19.Helper.Have(this.robot, 50 + this.robot.SPECS.UNITS[this.spawnOrder[this.positionInSpawnOrder]].CONSTRUCTION_KARBONITE, 500)) {
                var buildHere = bc19.Helper.RandomAdjacentNonResource(this.robot, this.robot.location);
                if (buildHere != null) {
                    output = this.robot.buildUnit(this.spawnOrder[this.positionInSpawnOrder], buildHere.x - this.robot.me.x, buildHere.y - this.robot.me.y);
                    this.positionInSpawnOrder++;
                }
            }
            var resources = this.ResourcesAround(this.robot, 10);
            var pilgrims = bc19.StationairyRobot.UnitAround(this.robot, this.robot.location, 10, this.robot.SPECS.PILGRIM);
            if (!bc19.Helper.EnemiesAround(this.robot) && resources > pilgrims) {
                var buildHere = bc19.Helper.RandomAdjacentNonResource(this.robot, this.robot.location);
                if (buildHere != null && bc19.Helper.Have(this.robot, 20, 100)) {
                    output = this.robot.buildUnit(this.robot.SPECS.PILGRIM, buildHere.x - this.robot.me.x, buildHere.y - this.robot.me.y);
                }
            }
            if (bc19.Helper.EnemiesAround(this.robot)) {
                var canBuildDefense = this.EvaluateEnemyRatio(this.robot);
                if (canBuildDefense != null) {
                    signal = this.CreateAttackSignal(bc19.Helper.closestEnemy(this.robot, bc19.Helper.EnemiesWithin(this.robot, this.robot.visionRange)), 8);
                    output = canBuildDefense;
                }
                else {
                    var enemiesAttacking = bc19.Helper.EnemiesWithin(this.robot, this.robot.attackRange[1]);
                    var closestEnemy = bc19.Helper.closestEnemy(this.robot, enemiesAttacking);
                    if (closestEnemy != null) {
                        output = this.robot.attack(closestEnemy.x - this.robot.me.x, closestEnemy.y - this.robot.me.y);
                    }
                }
            }
            if (signal > -1) {
                this.robot.signal(signal, 3);
            }
            return output;
        };
        return Church;
    }(bc19.StationairyRobot));
    bc19.Church = Church;
    Church["__class"] = "bc19.Church";
    Church["__interfaces"] = ["bc19.Machine"];
})(bc19 || (bc19 = {}));
(function (bc19) {
    var MyRobot = (function (_super) {
        __extends(MyRobot, _super);
        function MyRobot() {
            var _this = _super.call(this) || this;
            _this.robot = null;
            _this.location = null;
            _this.visionRange = 0;
            _this.tileVisionRange = 0;
            _this.attackRange = null;
            _this.tileAttackRange = null;
            _this.movementRange = 0;
            _this.tileMovementRange = 0;
            _this.fuelCapacity = 0;
            _this.karbCapacity = 0;
            _this.constructionKarb = 0;
            _this.constructionFuel = 0;
            _this.ourTeam = 0;
            _this.startHealth = 0;
            _this.currentHealth = 0;
            _this.previousLocation = null;
            _this.mapIsHorizontal = false;
            _this.positiveSide = false;
            _this.test = null;
            _this.testCount = 0;
            return _this;
        }
        MyRobot.prototype.turn = function () {
            if (this.me.turn === 1) {
                this.Setup();
            }
            this.previousLocation = this.location;
            this.location = new bc19.Position(this.me.y, this.me.x);
            this.currentHealth = this.me.health;
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
            return this.robot.Execute();
        };
        MyRobot.prototype.Setup = function () {
            this.visionRange = this.SPECS.UNITS[this.me.unit].VISION_RADIUS;
            this.tileVisionRange = (Math.sqrt(this.visionRange) | 0);
            this.attackRange = this.SPECS.UNITS[this.me.unit].ATTACK_RADIUS;
            this.tileAttackRange = this.attackRange != null ? [(Math.sqrt(this.attackRange[0]) | 0), (Math.sqrt(this.attackRange[1]) | 0)] : null;
            this.movementRange = this.SPECS.UNITS[this.me.unit].SPEED;
            this.tileMovementRange = (Math.sqrt(this.movementRange) | 0);
            this.fuelCapacity = this.SPECS.UNITS[this.me.unit].FUEL_CAPACITY;
            this.karbCapacity = this.SPECS.UNITS[this.me.unit].KARBONITE_CAPACITY;
            this.constructionFuel = this.SPECS.UNITS[this.me.unit].CONSTRUCTION_FUEL;
            this.constructionKarb = this.SPECS.UNITS[this.me.unit].CONSTRUCTION_KARBONITE;
            this.ourTeam = this.me.team === this.SPECS.RED ? 0 : 1;
            this.mapIsHorizontal = bc19.Helper.FindSymmetry(this.map);
            this.startHealth = this.SPECS.UNITS[this.me.unit].STARTING_HP;
            this.positiveSide = bc19.Helper.PositiveOrNegativeMap(this);
            this.previousLocation = new bc19.Position(this.me.y, this.me.x);
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
        /**
         *
         * @param {*} obj
         * @return {boolean}
         */
        Position.prototype.equals = function (obj) {
            if (!(obj != null && obj instanceof bc19.Position))
                return false;
            if (obj === this)
                return true;
            var toCompare = obj;
            return this.y === toCompare.y && this.x === toCompare.x;
        };
        Position.prototype.toString = function () {
            return "(" + ('' + (this.y)) + ", " + ('' + (this.x)) + ")";
        };
        return Position;
    }());
    bc19.Position = Position;
    Position["__class"] = "bc19.Position";
})(bc19 || (bc19 = {}));
//# sourceMappingURL=bundle.js.map
var specs = {"COMMUNICATION_BITS":16,"CASTLE_TALK_BITS":8,"MAX_ROUNDS":1000,"TRICKLE_FUEL":25,"INITIAL_KARBONITE":100,"INITIAL_FUEL":500,"MINE_FUEL_COST":1,"KARBONITE_YIELD":2,"FUEL_YIELD":10,"MAX_TRADE":1024,"MAX_BOARD_SIZE":64,"MAX_ID":4096,"CASTLE":0,"CHURCH":1,"PILGRIM":2,"CRUSADER":3,"PROPHET":4,"PREACHER":5,"RED":0,"BLUE":1,"CHESS_INITIAL":100,"CHESS_EXTRA":20,"TURN_MAX_TIME":200,"MAX_MEMORY":50000000,"UNITS":[{"CONSTRUCTION_KARBONITE":null,"CONSTRUCTION_FUEL":null,"KARBONITE_CAPACITY":null,"FUEL_CAPACITY":null,"SPEED":0,"FUEL_PER_MOVE":null,"STARTING_HP":200,"VISION_RADIUS":100,"ATTACK_DAMAGE":10,"ATTACK_RADIUS":[1,64],"ATTACK_FUEL_COST":10,"DAMAGE_SPREAD":0},{"CONSTRUCTION_KARBONITE":50,"CONSTRUCTION_FUEL":200,"KARBONITE_CAPACITY":null,"FUEL_CAPACITY":null,"SPEED":0,"FUEL_PER_MOVE":null,"STARTING_HP":100,"VISION_RADIUS":100,"ATTACK_DAMAGE":0,"ATTACK_RADIUS":0,"ATTACK_FUEL_COST":0,"DAMAGE_SPREAD":0},{"CONSTRUCTION_KARBONITE":10,"CONSTRUCTION_FUEL":50,"KARBONITE_CAPACITY":20,"FUEL_CAPACITY":100,"SPEED":4,"FUEL_PER_MOVE":1,"STARTING_HP":10,"VISION_RADIUS":100,"ATTACK_DAMAGE":null,"ATTACK_RADIUS":null,"ATTACK_FUEL_COST":null,"DAMAGE_SPREAD":null},{"CONSTRUCTION_KARBONITE":15,"CONSTRUCTION_FUEL":50,"KARBONITE_CAPACITY":20,"FUEL_CAPACITY":100,"SPEED":9,"FUEL_PER_MOVE":1,"STARTING_HP":40,"VISION_RADIUS":49,"ATTACK_DAMAGE":10,"ATTACK_RADIUS":[1,16],"ATTACK_FUEL_COST":10,"DAMAGE_SPREAD":0},{"CONSTRUCTION_KARBONITE":25,"CONSTRUCTION_FUEL":50,"KARBONITE_CAPACITY":20,"FUEL_CAPACITY":100,"SPEED":4,"FUEL_PER_MOVE":2,"STARTING_HP":20,"VISION_RADIUS":64,"ATTACK_DAMAGE":10,"ATTACK_RADIUS":[16,64],"ATTACK_FUEL_COST":25,"DAMAGE_SPREAD":0},{"CONSTRUCTION_KARBONITE":30,"CONSTRUCTION_FUEL":50,"KARBONITE_CAPACITY":20,"FUEL_CAPACITY":100,"SPEED":4,"FUEL_PER_MOVE":3,"STARTING_HP":60,"VISION_RADIUS":16,"ATTACK_DAMAGE":20,"ATTACK_RADIUS":[1,16],"ATTACK_FUEL_COST":15,"DAMAGE_SPREAD":3}]};
var robot = new bc19.MyRobot(); robot.setSpecs(specs);