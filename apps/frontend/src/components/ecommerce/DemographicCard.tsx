import { useState } from "react";
import { Dropdown } from "../ui/dropdown/Dropdown";
import { DropdownItem } from "../ui/dropdown/DropdownItem";
import { MoreDotIcon } from "../../icons";
import CountryMap from "./CountryMap";
import ANUCampusMap from "../maps/ANUCampusMap";

export default function DemographicCard() {
  const [isOpen, setIsOpen] = useState(false);

  function toggleDropdown() {
    setIsOpen(!isOpen);
  }

  function closeDropdown() {
    setIsOpen(false);
  }
  return (
    <div className="rounded-2xl border border-gray-200 bg-white p-5 dark:border-gray-800 dark:bg-white/[0.03] sm:p-6">
      <div className="flex justify-between">
        <div>
          <h3 className="text-lg font-semibold text-gray-800 dark:text-white/90">
            Scooter Operational Map
          </h3>
          <p className="mt-1 text-gray-500 text-theme-sm dark:text-gray-400">
            Scooter operational status across the ANU campus
          </p>
        </div>
        <div className="relative inline-block">
          <button className="dropdown-toggle" onClick={toggleDropdown}>
            <MoreDotIcon className="text-gray-400 hover:text-gray-700 dark:hover:text-gray-300 size-6" />
          </button>
          <Dropdown
            isOpen={isOpen}
            onClose={closeDropdown}
            className="w-40 p-2 z-[1001]"
          >
            <DropdownItem
              onItemClick={closeDropdown}
              className="flex w-full font-normal text-left text-gray-500 rounded-lg hover:bg-gray-100 hover:text-gray-700 dark:text-gray-400 dark:hover:bg-white/5 dark:hover:text-gray-300"
            >
              View More
            </DropdownItem>
            <DropdownItem
              onItemClick={closeDropdown}
              className="flex w-full font-normal text-left text-gray-500 rounded-lg hover:bg-gray-100 hover:text-gray-700 dark:text-gray-400 dark:hover:bg-white/5 dark:hover:text-gray-300"
            >
              Delete
            </DropdownItem>
          </Dropdown>
        </div>
      </div>
      {/* <div className="px-4 py-6 my-6 overflow-hidden border border-gary-200 rounded-2xl dark:border-gray-800 sm:px-6">
        <div
          id="mapOne"
          className="mapOne map-btn -mx-4 -my-6 h-[212px] w-[252px] 2xsm:w-[307px] xsm:w-[358px] sm:-mx-6 md:w-[668px] lg:w-[634px] xl:w-[393px] 2xl:w-[554px]"
        >
          <CountryMap />
        </div>
      </div> */}
      {/* ANU Campus Map - replacing the commented CountryMap */}
      <div className="my-6 overflow-hidden border border-gray-200 rounded-2xl dark:border-gray-800">
        <div
          id="mapOne"
          className="mapOne map-btn h-[500px] w-full"
        >
          <ANUCampusMap />
        </div>
      </div>

      {/* Updated statistics for scooter status instead of countries */}
      <div className="space-y-5">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="flex h-8 w-8 items-center justify-center rounded-full bg-green-100 dark:bg-green-900/20">
              <div className="h-3 w-3 rounded-full bg-green-500"></div>
            </div>
            <div>
              <p className="font-semibold text-gray-800 text-theme-sm dark:text-white/90">
                Running Scooters
              </p>
              <span className="block text-gray-500 text-theme-xs dark:text-gray-400">
                24 Available for rent
              </span>
            </div>
          </div>

          <div className="flex w-full max-w-[140px] items-center gap-3">
            <div className="relative block h-2 w-full max-w-[100px] rounded-sm bg-gray-200 dark:bg-gray-800">
              <div className="absolute left-0 top-0 flex h-full w-[65%] items-center justify-center rounded-sm bg-green-500 text-xs font-medium text-white"></div>
            </div>
            <p className="font-medium text-gray-800 text-theme-sm dark:text-white/90">
              65%
            </p>
          </div>
        </div>

        <div className="flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="flex h-8 w-8 items-center justify-center rounded-full bg-yellow-100 dark:bg-yellow-900/20">
              <div className="h-3 w-3 rounded-full bg-yellow-500"></div>
            </div>
            <div>
              <p className="font-semibold text-gray-800 text-theme-sm dark:text-white/90">
                Locked Scooters
              </p>
              <span className="block text-gray-500 text-theme-xs dark:text-gray-400">
                9 Currently in use
              </span>
            </div>
          </div>

          <div className="flex w-full max-w-[140px] items-center gap-3">
            <div className="relative block h-2 w-full max-w-[100px] rounded-sm bg-gray-200 dark:bg-gray-800">
              <div className="absolute left-0 top-0 flex h-full w-[25%] items-center justify-center rounded-sm bg-yellow-500 text-xs font-medium text-white"></div>
            </div>
            <p className="font-medium text-gray-800 text-theme-sm dark:text-white/90">
              25%
            </p>
          </div>
        </div>

        <div className="flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="flex h-8 w-8 items-center justify-center rounded-full bg-red-100 dark:bg-red-900/20">
              <div className="h-3 w-3 rounded-full bg-red-500"></div>
            </div>
            <div>
              <p className="font-semibold text-gray-800 text-theme-sm dark:text-white/90">
                Under Maintenance
              </p>
              <span className="block text-gray-500 text-theme-xs dark:text-gray-400">
                4 Being serviced
              </span>
            </div>
          </div>

          <div className="flex w-full max-w-[140px] items-center gap-3">
            <div className="relative block h-2 w-full max-w-[100px] rounded-sm bg-gray-200 dark:bg-gray-800">
              <div className="absolute left-0 top-0 flex h-full w-[10%] items-center justify-center rounded-sm bg-red-500 text-xs font-medium text-white"></div>
            </div>
            <p className="font-medium text-gray-800 text-theme-sm dark:text-white/90">
              10%
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}
