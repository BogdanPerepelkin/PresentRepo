class ICar
{
public:

    virtual void EngineStart() = 0;

    virtual void EngineStop() = 0;

    virtual void Refuel(double liters) = 0;

    virtual void RunningIdle() = 0;

protected:

    bool engineIsRunning = false;
};

class IEngine {
public:

    virtual void Consume(double liters) = 0;

    virtual void Start() = 0;

    virtual void Stop() = 0;

protected:

    bool isRunning = false;
};

class IFuelTank {
public:

    virtual void Consume(double liters) = 0;

    virtual void Refuel(double liters) = 0;

protected:

    double fillLevel = 0.0;

    bool isOnReserve = false;

    bool isComplete = false;
};

class IFuelTankDisplay {
protected:

    double fillLevel = 0.0;

    bool isOnReserve = false;

    bool isComplete = false;
};

// =====================================================================
#include <cmath>

class FuelTank : public IFuelTank {
    void check_flags() {
        if (fillLevel <= 5.0) {
            isOnReserve = true;
        }
        if (fillLevel >= 60.0) {
            isComplete = true;
            fillLevel = 60.0;
        }
        if (fillLevel < 0.0) {
            fillLevel = 0.0;
        }
    }
public:
    FuelTank(double start_litters) {
        fillLevel = start_litters;
        check_flags();
    }

    void Refuel(double liters) override {
        fillLevel += liters;
        check_flags();
    }

    void Consume(double liters) override {
        fillLevel -= liters;
        check_flags();
    }

    double getLevel() {
        return fillLevel;
    }

    bool getIsOnReserve() {
        return isOnReserve;
    }

    bool getIsComplete() {
        return isComplete;
    }
};

class Engine : public IEngine {
    FuelTank *tank = nullptr;

public:
    Engine(FuelTank *tank) {
        this->tank = tank;
    }

    void Start() override {
        if (tank->getLevel() > 0.0) {
            isRunning = true;
        }
    }

    void Stop() override {
        isRunning = false;
    }

    void Consume(const double liters) override {
        if (isRunning) {
            tank->Consume(liters);
            if (tank->getLevel() <= 0.0) {
                isRunning = false;
            }
        }
    }

    bool isEngineRunning() {
        return isRunning;
    }
};

class FuelTankDisplay : public IFuelTankDisplay {
    FuelTank *tank = nullptr;

public:
    FuelTankDisplay(FuelTank *tank) {
        this->tank = tank;
    }

    double getFillLevel() {
        return std::round(tank->getLevel() * 100.0) / 100.0;
    }

    bool getIsOnReserve() {
        return tank->getIsOnReserve();
    }

    bool getIsComplete() {
        return tank->getIsComplete();
    }
};

class Car : public ICar {
    FuelTank tank;
    Engine engine;

public:
    FuelTankDisplay *fuelTankDisplay;
    Car(double startLevel = 20.0) : tank(startLevel), engine(&tank) {
        engineIsRunning = false;
        fuelTankDisplay = new FuelTankDisplay(&tank);
    }

    ~Car() {
        delete fuelTankDisplay;
    }

    void EngineStart() override {
        engine.Start();
        engineIsRunning = engine.isEngineRunning();
    }

    void EngineStop() override {
        engine.Stop();
        engineIsRunning = engine.isEngineRunning();
    }

    void Refuel(double liters) override {
        tank.Refuel(liters);
    }

    void RunningIdle() override {
        engine.Consume(0.0003);
        if (tank.getLevel() <= 0.0) {
            engine.Stop();
        }
    }

    bool getEngineIsRunning() {
        return engine.isEngineRunning();
    }
};

